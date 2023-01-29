package com.example.commander.raw.result;

import com.example.commander.Pod;

import java.util.List;

public record PodResults(Pod pod, List<RawResult> results) implements RawPodResults {
}
