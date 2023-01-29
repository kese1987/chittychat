package com.example.commander.sender.command;

import com.example.commander.Pod;
import com.example.commander.result.RawPodResults;
import com.example.commander.result.RawResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncCommandManager {

    CompletableFuture<List<RawPodResults>> runAsync(byte[] command);



}
