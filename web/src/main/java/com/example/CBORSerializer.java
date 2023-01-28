package com.example;

import com.example.commands.ExecutableCommands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;

public class CBORSerializer {

    private final ObjectMapper mapper;

    public CBORSerializer(ObjectMapper cborMapper) {
        this.mapper = cborMapper;
    }

    public <T> T deserialize(byte[] object, Class<T> clazz){
        try {
            return mapper.readValue(object, clazz);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public byte[] serialize(Object t){
        try {
            return mapper.writeValueAsBytes(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
