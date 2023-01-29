package com.example.commander.sender.k8s;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DefaultHostnameSupplier implements HostnameSupplier {
    @Override
    public String get() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
