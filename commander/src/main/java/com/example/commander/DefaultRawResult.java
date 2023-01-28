package com.example.commander;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record DefaultRawResult(@JsonProperty byte[] result) implements RawResult{

    @JsonCreator
    public DefaultRawResult(@JsonProperty("result") byte[] result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultRawResult that = (DefaultRawResult) o;
        return Arrays.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(result);
    }
}
