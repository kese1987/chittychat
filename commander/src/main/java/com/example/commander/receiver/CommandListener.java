package com.example.commander.receiver;

import com.example.commander.DefaultRawResult;
import com.example.commander.RawCommand;
import com.example.commander.RawResult;

import java.util.concurrent.CompletableFuture;

public interface CommandListener {
    boolean supports(byte[] rawCommand);
    CompletableFuture<DefaultRawResult> onCommand(byte[] rawCommand);
}
