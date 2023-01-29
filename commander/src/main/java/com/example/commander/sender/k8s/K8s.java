package com.example.commander.sender.k8s;

import com.example.commander.Pod;

import java.util.List;

public interface K8s {

    Pod runningPod();
    List<Pod> pods();

}
