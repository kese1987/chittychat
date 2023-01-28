package com.example.commander.sender;

import com.example.commander.DefaultPod;
import com.example.commander.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.apache.commons.lang3.concurrent.AtomicInitializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DefaultK8s implements K8s {

    private final KubernetesClient client;
    private final AtomicInitializer<String> namespace;

    public DefaultK8s(KubernetesClient client) {
        this.client = client;
        namespace = new AtomicInitializer<String>() {
            @Override
            protected String initialize() {
                return client.getConfiguration().getNamespace();
            }
        };
    }

    @Override
    public List<com.example.commander.Pod> pods() {

        /*return Arrays.asList(new DefaultPod("127.0.0.1:8080", "test-localhost"));*/


        try {
            NonNamespaceOperation<io.fabric8.kubernetes.api.model.Pod, PodList, PodResource> appPods = client.pods().inNamespace(namespace.get());
            return appPods
                    .list()
                    .getItems()
                    .stream()
                    .map(it -> new DefaultPod(it.getStatus().getPodIP(), it.getMetadata().getName()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
