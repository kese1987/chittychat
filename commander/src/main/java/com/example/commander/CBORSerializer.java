package com.example.commander;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CBORSerializer implements CommandSerializer {

    private final ObjectMapper mapper;

    public CBORSerializer(ObjectMapper cborMapper) {
        this.mapper = cborMapper;
    }

    @Override
    public <T> T deserialize(byte[] object){
        try {
            return mapper.readValue(object, new TypeReference<>() {});
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serialize(Object t){
        try {
            return mapper.writeValueAsBytes(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
