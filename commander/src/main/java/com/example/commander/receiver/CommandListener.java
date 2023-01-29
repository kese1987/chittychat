package com.example.commander.receiver;

import com.example.commander.result.HandlerResult;
import com.example.commander.result.RawResult;

import java.util.concurrent.CompletableFuture;

public interface CommandListener {
    boolean supports(byte[] rawCommand);
    CompletableFuture<RawResult> onCommand(byte[] rawCommand);

    String id();
}
