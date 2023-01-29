package com.example.commands;

import com.example.commander.CBORSerializer;
import com.example.commander.result.HandlerResult;
import com.example.commander.receiver.CommandListener;
import com.example.commander.result.RawResult;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PingListener implements CommandListener {

    private final CBORSerializer serde;
    private String instance;
    private final Map<byte[], Ping> commands = new HashMap<>();


    public PingListener(CBORSerializer serde, String instance) {
        this.serde = serde;
        this.instance = instance;
    }

    @Override
    public boolean supports(byte[] rawCommand) {

        var command = serde.deserialize(rawCommand, new TypeReference<ExecutableCommands>() {});

        if(command instanceof Ping p){
            commands.put(rawCommand, p);
            return true;
        }

        return false;
    }

    @Override
    public CompletableFuture<RawResult> onCommand(byte[] rawCommand) {

        Ping ping = commands.get(rawCommand);

        byte[] result = serde.serialize(new Ping.Result(ping.masterMessage() + "-> Hello Master, pong (" + instance + ")!"),
                new TypeReference<Ping.Result>() {});

        return CompletableFuture.completedFuture(new HandlerResult(instance, result));

    }

    @Override
    public String id() {
        return instance;
    }
}
