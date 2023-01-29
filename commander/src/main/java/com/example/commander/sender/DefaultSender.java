package com.example.commander.sender;

import com.example.commander.*;
import com.example.commander.result.RawPodResults;
import com.example.commander.sender.command.SyncCommandManager;
import com.example.commander.sender.command.AsyncCommandManager;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;


public class DefaultSender implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSender.class);

    private final AsyncCommandManager asyncCommandManager;
    private final SyncCommandManager syncCommandManager;
    private final CommandSerializer serde;


    public DefaultSender(AsyncCommandManager asyncCommandManager,
                         SyncCommandManager syncCommandManager,
                         CommandSerializer serde)  {


        this.asyncCommandManager = asyncCommandManager;
        this.syncCommandManager = syncCommandManager;
        this.serde = serde;
    }

    @Override
    public List<RawPodResults> run(byte[] command){

        byte[] serializedCommand = serializeCommand(command);

        try {
            return syncCommandManager.run(serializedCommand);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    private byte[] serializeCommand(byte[] command) {
        return serde.serialize(new RawCommand(command, "signature".getBytes(StandardCharsets.UTF_8)), new TypeReference<RawCommand>() {
        });
    }

    @Override
    public CompletableFuture<List<RawPodResults>> runAsync(byte[] command) {
        byte[] serializedCommand = serializeCommand(command);

        try {
            return asyncCommandManager.runAsync(serializedCommand);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
