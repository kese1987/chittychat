package com.example.commander;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CommandSerializer {

    <T> T deserialize(byte[] object, TypeReference<T> type);

    <T> byte[] serialize(Object t, TypeReference<T> type);
}
