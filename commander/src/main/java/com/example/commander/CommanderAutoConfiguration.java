package com.example.commander;

import com.example.commander.sender.*;
import com.example.commander.sender.command.DefaultAsyncCommandManager;
import com.example.commander.sender.command.DefaultSyncCommandManager;
import com.example.commander.sender.k8s.DefaultHostnameSupplier;
import com.example.commander.sender.k8s.DefaultK8s;
import com.example.commander.sender.k8s.DefaultNamespaceSupplier;
import com.example.commander.sender.k8s.K8s;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommanderAutoConfiguration {

    @Bean
    CommandSerializer commandSerializer() {
        return new CBORSerializer(new ObjectMapper(new CBORFactory()));
    }

    @Bean
    @ConditionalOnMissingBean
    K8s kube() {
        var kubeClient = new DefaultKubernetesClient();
        return new DefaultK8s(kubeClient, new DefaultNamespaceSupplier(kubeClient),8080, new DefaultHostnameSupplier());
    }

    @Bean
    Sender sender(CommandSerializer commandSerializer, K8s kube) {

        var executorService = Executors.newFixedThreadPool(5);
        var scheduledService = Executors.newScheduledThreadPool(1);
        var client = new RestTemplateBuilder().build();


        var asyncManager = new DefaultAsyncCommandManager(executorService, client, scheduledService, kube);
        var syncManager = new DefaultSyncCommandManager(kube, executorService, client, commandSerializer);

        return new DefaultSender(asyncManager, syncManager, commandSerializer);
    }



}
