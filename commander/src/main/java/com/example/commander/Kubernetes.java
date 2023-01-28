package com.example.commander;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;

import java.util.List;
import java.util.stream.Collectors;

public class Kubernetes {

    public static String getNamespace(){
        try (KubernetesClient k8s = new DefaultKubernetesClient()) {
            // This should print namespace loaded in /var/run/secrets/kubernetes.io/serviceaccount/namespace
            return k8s.getConfiguration().getNamespace();
        }
    }

    public static List<String> pods() {

        String namespace = getNamespace();
        try (KubernetesClient k8s = new DefaultKubernetesClient()) {
            // This should print namespace loaded in /var/run/secrets/kubernetes.io/serviceaccount/namespace
            NonNamespaceOperation<Pod, PodList, PodResource> appPods = k8s.pods().inNamespace(namespace);
            return appPods.list().getItems().stream().map(it -> "" + it.getStatus().getPodIP() + ":" + it.getMetadata().getName() ).collect(Collectors.toList());
        }

    }

}
