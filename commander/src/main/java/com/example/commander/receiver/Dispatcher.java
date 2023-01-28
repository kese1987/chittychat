package com.example.commander.receiver;

import com.example.commander.DefaultRawCommand;
import com.example.commander.DefaultRawResult;
import com.example.commander.RawCommand;
import com.example.commander.RawResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class Dispatcher {

    private final List<CommandListener> listeners;
    private ObjectMapper mapper;

    public Dispatcher(List<CommandListener> listeners,
                      ObjectMapper mapper) {
        
        this.listeners = listeners;
        this.mapper = mapper;
    }

    @PostMapping("runCommand")
    CompletableFuture<byte[]> exec(@RequestBody byte[] cmd) {

        try {

            RawCommand command = mapper.readValue(cmd, DefaultRawCommand.class);


            List<CompletableFuture<DefaultRawResult>> commandsResult = listeners.stream()
                    .filter(listener -> listener.supports(command.command()))
                    .map(listener -> listener.onCommand(command.command()))
                    .toList();


            CompletableFuture<byte[]> listCompletableFuture = CompletableFuture.allOf(commandsResult.toArray(new CompletableFuture[0]))
                    .thenApply(__ -> {
                        List<DefaultRawResult> results = commandsResult
                            .stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .toList();
                        try {
                            return mapper.writeValueAsBytes(results);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

            return listCompletableFuture;
        } catch (Exception e){
            return CompletableFuture.failedFuture(e);
        }
    }
}
