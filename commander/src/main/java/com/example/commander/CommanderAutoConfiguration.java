package com.example.commander;

import com.example.commander.sender.DefaultK8s;
import com.example.commander.sender.DefaultSender;
import com.example.commander.sender.K8s;
import com.example.commander.sender.Sender;
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

        return new DefaultSender(
                new DefaultK8s(new DefaultKubernetesClient(), 8080),
                Executors.newFixedThreadPool(5),
                new RestTemplateBuilder().build(),
                cborMapper);
    }



}
