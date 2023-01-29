package com.example.commander.receiver;

import com.example.commander.raw.result.HandlerResult;

import java.util.concurrent.CompletableFuture;

public interface CommandListener {
    boolean supports(byte[] rawCommand);
    CompletableFuture<HandlerResult> onCommand(byte[] rawCommand);
}
