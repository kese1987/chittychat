package com.example.commander;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public record RawCommand(
        @JsonProperty byte[] command,
        @JsonProperty byte[] signature
){

    @JsonCreator
    public RawCommand(
            @JsonProperty("command") byte[] command,
            @JsonProperty("signature") byte[] signature) {
        this.command = command;
        this.signature = signature;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawCommand that = (RawCommand) o;
        return Arrays.equals(command, that.command) && Arrays.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(command);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
}
