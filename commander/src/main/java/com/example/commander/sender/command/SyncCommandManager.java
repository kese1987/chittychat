package com.example.commander.sender.command;

import com.example.commander.result.RawPodResults;

import java.util.List;

public interface SyncCommandManager {
    List<RawPodResults> run(byte[] command);
}
