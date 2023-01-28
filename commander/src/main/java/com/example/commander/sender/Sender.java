package com.example.commander.sender;

import com.example.commander.PodResult;
import com.example.commander.RawCommand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Sender {
    CompletableFuture<List<PodResult>> send(byte[] command);

}
