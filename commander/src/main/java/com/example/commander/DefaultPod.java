package com.example.commander;

import java.util.Objects;

public class DefaultPod implements Pod {

    private final String address;
    private final String name;

    public DefaultPod(String address, String name) {
        this.address = address;
        this.name = name;
    }

    @Override
    public String getAddress() {
        return address;
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
        return Objects.equals(address, that.address) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, name);
    }

    @Override
    public String toString() {
        return "DefaultPod{" +
                "ip='" + address + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
