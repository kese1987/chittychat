package com.example.commander;

import java.util.Objects;

public class DefaultPod implements Pod {

    private final String ip;
    private final String name;

    public DefaultPod(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPod that = (DefaultPod) o;
        return Objects.equals(ip, that.ip) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, name);
    }

    @Override
    public String toString() {
        return "DefaultPod{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
