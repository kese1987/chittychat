package com.example.commander.sender;

import com.example.commander.DefaultRawResult;
import com.example.commander.Pod;
import com.example.commander.PodResult;
import com.example.commander.RawResult;

import java.util.List;

class DefaultPodResult implements PodResult {

    private final Pod pod;
    private final List<DefaultRawResult> rawResult;

    public DefaultPodResult(Pod pod, List<DefaultRawResult> rawResults) {
        this.pod = pod;
        this.rawResult = rawResults;
    }

    @Override
    public Pod pod() {
        return pod;
    }

    @Override
    public List<DefaultRawResult> results() {
        return rawResult;
    }
}
