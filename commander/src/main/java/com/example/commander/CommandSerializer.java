package com.example.commander;

public interface CommandSerializer {
    <T> T deserialize(byte[] object);

    byte[] serialize(Object t);
}
