package com.example.commander.result;

import com.example.commander.Pod;

public record PodException(Pod pod, Throwable exception) implements RawPodResults {
}
