package com.example.commander;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class Kubernetes {

    public static String getNamespace(){
        try (KubernetesClient k8s = new DefaultKubernetesClient()) {
            // This should print namespace loaded in /var/run/secrets/kubernetes.io/serviceaccount/namespace
            return k8s.getConfiguration().getNamespace();
        }
    }

}
