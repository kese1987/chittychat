package com.example.commander.raw.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record HandlerResult(@JsonProperty byte[] result) implements RawResult {

    @JsonCreator
    public HandlerResult(@JsonProperty("result") byte[] result) {
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
