package com.example.commander.sender.k8s;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.concurrent.AtomicInitializer;

public class DefaultNamespaceSupplier implements NamespaceSupplier {

    private final KubernetesClient client;
    private final AtomicInitializer<String> namespace;

    public DefaultNamespaceSupplier(KubernetesClient client) {
        this.client = client;
        namespace = new AtomicInitializer<String>() {
            @Override
            protected String initialize() {
                return client.getConfiguration().getNamespace();
            }
        };
    }

    @Override
    public String get() {
        try {

            return namespace.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
