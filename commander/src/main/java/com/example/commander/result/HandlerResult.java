package com.example.commander.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record HandlerResult(String id, @JsonProperty byte[] result) implements RawResult {

    @JsonCreator
    public HandlerResult(String id, @JsonProperty("result") byte[] result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerResult that = (HandlerResult) o;
        return Arrays.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(result);
    }

    @Override
    public String toString() {
        return "HandlerResult{" +
                "result=" + Arrays.toString(result) +
                '}';
    }
}
