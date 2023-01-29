package com.example.commander.receiver;

import com.example.commander.CommandSerializer;
import com.example.commander.RawCommand;
import com.example.commander.result.HandlerException;
import com.example.commander.result.RawResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
    private final List<CommandListener> listeners;
    private final CommandSerializer commandSerializer;

    public CommandController(List<CommandListener> listeners,
                             CommandSerializer commandSerializer) {
        
        this.listeners = listeners;
        this.commandSerializer = commandSerializer;
    }

    @PostMapping("run")
    ResponseEntity<byte[]> run(@RequestBody byte[] cmd) {

        try {

            RawCommand command = commandSerializer.deserialize(cmd, new TypeReference<>() {});


            List<RawResult> commandsResult = listeners.stream()
                    .filter(listener -> listener.supports(command.command()))
                    .map(listener -> Pair.of(listener, listener.onCommand(command.command())))
                    .map(envelope -> {

                        try {
                          return envelope.getValue().join();
                        } catch (Exception e){
                            return new HandlerException(envelope.getKey().id(), e);
                        }
                    })
                    .toList();

            return ok(commandSerializer.serialize(commandsResult, new TypeReference<List<RawResult>>() {}));
        } catch (Exception e){
            logger.error("", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
