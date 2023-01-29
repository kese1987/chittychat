package com.example.commander.sender.command;

import com.example.commander.result.RawResult;

import java.util.List;

public interface CallableAsyncCommandManager extends AsyncCommandManager{
    void callback(String jobId, String podId, List<RawResult> result);
}
