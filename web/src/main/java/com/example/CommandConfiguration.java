package com.example;

import com.example.commander.CBORSerializer;
import com.example.commander.receiver.CommandListener;
import com.example.commands.PingListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    CommandListener handler1(CBORSerializer serde) {
        return new PingListener(serde, "handler1");
    }
    @Bean
    CommandListener handler2(CBORSerializer serde) {
        return new PingListener(serde, "handler2");
    }




}
