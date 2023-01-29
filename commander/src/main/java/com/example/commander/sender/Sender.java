package com.example.commander.sender;

import com.example.commander.result.RawPodResults;
import com.example.commander.sender.command.AsyncCommandManager;
import com.example.commander.sender.command.SyncCommandManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Sender extends AsyncCommandManager, SyncCommandManager {

}
