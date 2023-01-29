package com.example.commander.sender;

import com.example.commander.raw.result.RawPodResults;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Sender {
    CompletableFuture<List<RawPodResults>> run(byte[] command);
    CompletableFuture<List<RawPodResults>> runAsync(byte[] command);
}
