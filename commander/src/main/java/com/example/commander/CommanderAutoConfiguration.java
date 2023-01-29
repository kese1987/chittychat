package com.example.commander;

import com.example.commander.sender.*;
import com.example.commander.sender.command.CallableAsyncCommandManager;
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
import org.springframework.web.client.RestOperations;

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
    RestOperations restOperations(){
        return new RestTemplateBuilder().build();
    }

    @Bean
    ExecutorService executorService(){
        return Executors.newFixedThreadPool(5);
    }

    @Bean
    CallableAsyncCommandManager callableAsyncCommandManager(RestOperations client,
                                                            ExecutorService executorService,
                                                            K8s kube){
        var scheduledService = Executors.newScheduledThreadPool(1);
        return new DefaultAsyncCommandManager(executorService, client, scheduledService, kube);
    }

    @Bean
    Sender sender(CommandSerializer commandSerializer,
                  K8s kube,
                  ExecutorService executorService,
                  RestOperations client,
                  CallableAsyncCommandManager callableAsyncCommandManager) {





        var syncManager = new DefaultSyncCommandManager(kube, executorService, client, commandSerializer);

        return new DefaultSender(callableAsyncCommandManager, syncManager, commandSerializer);
    }



}
