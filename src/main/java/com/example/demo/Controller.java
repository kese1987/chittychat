package com.example.demo;

import com.example.commander.Kubernetes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class Controller {

    @GetMapping("/ping")
    public String ping() throws UnknownHostException {

        return InetAddress.getLocalHost().getHostName() + ": pong (" + Kubernetes.getNamespace() + ")";
    }

}
