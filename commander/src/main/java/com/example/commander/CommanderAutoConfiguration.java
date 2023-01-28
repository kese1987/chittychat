package com.example.commander;

import com.example.commander.sender.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.Executors;

@Configuration
public class CommanderAutoConfiguration {

    @Bean
    ObjectMapper cborMapper() {
        return new ObjectMapper(new CBORFactory());
    }

    @Bean
    Sender sender(ObjectMapper cborMapper) {

        DefaultKubernetesClient client = new DefaultKubernetesClient();
        return new DefaultSender(
                new DefaultK8s(client, new DefaultNamespaceSupplier(client),8080, new DefaultHostnameSupplier()),
                Executors.newFixedThreadPool(5),
                new RestTemplateBuilder().build(),
                cborMapper);
    }



}
