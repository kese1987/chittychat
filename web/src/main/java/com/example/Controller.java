package com.example;

import com.example.commander.CBORSerializer;
import com.example.commander.sender.Sender;
import com.example.commands.Ping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {

    private final Sender sender;
    private CBORSerializer serializer;

    public Controller(Sender sender,
                      CBORSerializer serializer) {
        this.sender = sender;
        this.serializer = serializer;
    }

    @GetMapping(value = "ping", produces = {"application/json"})
    public CompletableFuture<String> ping() {

        byte[] ping = serializer.serialize(new Ping("Hello neighbor, I'm your master!"));

        return sender.run(ping)
                .thenApply(results -> results.stream().map(it -> {
                    String msg = it.pod().getName() + "[" + it.pod().getAddress() + "] said: ";
                    List<String> strings = it.results().stream()
                            .map(result -> serializer.deserialize(result.result(), Ping.Result.class))
                            .map(Ping.Result::response).toList();

                    return msg + strings;

                }).toList().toString())
                .exceptionally(Throwable::getLocalizedMessage);


    }



}
