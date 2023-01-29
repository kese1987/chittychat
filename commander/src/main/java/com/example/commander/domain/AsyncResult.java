package com.example.commander.domain;

import com.example.commander.result.RawResult;

import java.util.List;

public record AsyncResult(JobId jobId, PodId podId, List<RawResult> results) {
}
