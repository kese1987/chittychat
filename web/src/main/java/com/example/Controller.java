package com.example;

import com.example.commander.CommandSerializer;
import com.example.commander.result.*;
import com.example.commander.sender.Sender;
import com.example.commands.Ping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
public class Controller {

    private final Sender sender;
    private CommandSerializer serializer;
    private final ObjectMapper objectMapper;

    public Controller(Sender sender,
                      CommandSerializer serializer,
                      ObjectMapper objectMapper) {
        this.sender = sender;
        this.serializer = serializer;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "async", produces = {"application/json"})
    public CompletableFuture<ResponseEntity<String>> async() {
        byte[] ping = serializer.serialize(new Ping("Hello neighbor, I'm your master!"), new TypeReference<Ping>() {
        });

        return sender.runAsync(ping).thenApply(results -> {
            try {
                return ResponseEntity.ok(objectMapper.writeValueAsString(toMap(results)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    @GetMapping(value = "ping", produces = {"application/json"})
    public ResponseEntity<String> ping() {

        byte[] ping = serializer.serialize(new Ping("Hello neighbor, I'm your master!"), new TypeReference<Ping>() {
        });

        var results = sender.run(ping);

        try {
            return ResponseEntity.ok(objectMapper.writeValueAsString(toMap(results)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Map<String, String>> toMap(List<RawPodResults> results) {
        return results
        .stream()
        .map(it -> {
            if(it instanceof PodException e){
                return Pair.of(e.pod().hostname(), Map.of("Failure reason",e.exception().getMessage()));
            } else if (it instanceof PodResults p) {
                var handlers = p.results().stream().map(rr -> {
                    if(rr instanceof HandlerException he){
                        return Pair.of(he.id(), he.exception().getMessage());
                    } else if (rr instanceof HandlerResult hr) {
                        var rs = serializer.deserialize(hr.result(), new TypeReference<Ping.Result>() {});
                        return Pair.of(hr.id(), rs.response());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

                return Pair.of(p.pod().hostname(), handlers);
            } else {
                return null;
            }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }


}
