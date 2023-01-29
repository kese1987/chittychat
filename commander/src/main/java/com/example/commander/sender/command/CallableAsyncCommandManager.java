package com.example.commander.sender.command;

import com.example.commander.domain.AsyncResult;
import com.example.commander.result.RawResult;

import java.util.List;

public interface CallableAsyncCommandManager extends AsyncCommandManager{
    void callback(AsyncResult result);
}
