package com.example.commander;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CBORSerializer implements CommandSerializer {

    private final ObjectMapper mapper;

    public CBORSerializer(ObjectMapper cborMapper) {
        this.mapper = cborMapper;
    }

    @Override
    public <T> T deserialize(byte[] object, TypeReference<T> type){
        try {
            return mapper.readValue(object, type);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> byte[] serialize(Object t, TypeReference<T> type){
        try {
            return mapper.writerFor(type).writeValueAsBytes(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
