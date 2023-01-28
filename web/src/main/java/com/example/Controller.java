package com.example;

import com.example.commander.sender.Sender;
import com.example.commands.Ping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {

    private final Sender sender;
    private CBORSerializer serializer;
    private ObjectMapper mapper;

    public Controller(Sender sender,
                      CBORSerializer serializer,
                      ObjectMapper mapper) {
        this.sender = sender;
        this.serializer = serializer;
        this.mapper = mapper;
    }

    @GetMapping(value = "ping", produces = {"application/json"})
    public CompletableFuture<String> ping() {

        byte[] ping = serializer.serialize(new Ping("Hello neighbor, I'm your master!"));

        return sender.send(ping)
                .thenApply(results -> {
                    List<String> replies = results.stream().map(it -> {
                        String msg = it.pod().getName() + "[" + it.pod().getAddress() + "] said: ";
                        List<String> strings = it.results().stream()
                                .map(result -> serializer.deserialize(result.result(), Ping.Result.class))
                                .map(Ping.Result::response).toList();

                        return msg + strings;

                    }).toList();
                    try {
                        return mapper.writeValueAsString(replies);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(Throwable::getLocalizedMessage);


    }



}
