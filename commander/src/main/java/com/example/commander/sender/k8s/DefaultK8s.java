package com.example.commander.sender.k8s;

import com.example.commander.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultK8s implements K8s {

    private final KubernetesClient client;
    private final NamespaceSupplier namespace;

    private int port;
    private HostnameSupplier hostnameSupplier;

    public DefaultK8s(KubernetesClient client,
                      NamespaceSupplier namespaceSupplier,
                      int port,
                      HostnameSupplier hostnameSupplier) {
        this.client = client;
        this.namespace = namespaceSupplier;
        this.port = port;
        this.hostnameSupplier = hostnameSupplier;
    }

    @Override
    public List<com.example.commander.Pod> pods() {

        /*return Arrays.asList(new DefaultPod("127.0.0.1:8080", "test-localhost"));*/


        try {
            String hostname = hostnameSupplier.get();
            NonNamespaceOperation<io.fabric8.kubernetes.api.model.Pod, PodList, PodResource> appPods = client.pods().inNamespace(namespace.get());
            return appPods
                    .list()
                    .getItems()
                    .stream()
                    .filter(it -> !it.getMetadata().getName().equals(hostname))
                    .map(it -> new Pod(it.getStatus().getPodIP().replace(".", "-") + "." + namespace.get() + ".pod:" + port, it.getMetadata().getName()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
