package com.example;

import com.example.commander.Pod;
import com.example.commander.sender.k8s.K8s;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("local")
public class LocalConfiguration {

    @Bean
    K8s kube() {
        return new K8s() {
            @Override
            public List<Pod> pods() {
                return List.of(new Pod("127.0.0.1:8080", "localhost"));
            }
        };
    }
}
