package com.example.commander.sender.k8s;


import com.example.commander.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.concurrent.AtomicInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.util.List;
import java.util.function.Predicate;


public class DefaultK8s implements K8s {

    private final KubernetesClient client;
    private final NamespaceSupplier namespace;

    private int port;
    private HostnameSupplier hostnameSupplier;
    private AtomicInitializer<Pod> runningPod;

    public DefaultK8s(KubernetesClient client,
                      NamespaceSupplier namespaceSupplier,
                      int port,
                      HostnameSupplier hostnameSupplier) {
        this.client = client;
        this.namespace = namespaceSupplier;
        this.port = port;
        this.hostnameSupplier = hostnameSupplier;
        this.runningPod = new AtomicInitializer<Pod>() {
            @Override
            protected Pod initialize() throws ConcurrentException {
                return podsMatching(hostnameSupplier.get(), pod -> pod.getMetadata().getName().equals(hostnameSupplier.get())).stream().findFirst().orElse(null);
            }
        };


    }

    @Override
    public Pod runningPod() {
        try {
            return runningPod.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<com.example.commander.Pod> pods() {

        try {
            return podsMatching(hostnameSupplier.get(), pod -> !pod.getMetadata().getName().equals(hostnameSupplier.get()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Pod> podsMatching(String namespace, Predicate<io.fabric8.kubernetes.api.model.Pod> condition) {

        return client.pods().inNamespace(namespace)
                .list()
                .getItems()
                .stream()
                .filter(condition)
                .map(it -> new Pod(it.getStatus().getPodIP().replace(".", "-") + "." + namespace + ".pod:" + port, it.getMetadata().getName()))
                .toList();
    }

}
