package com.example.commander.result;

public record HandlerException(String id, Throwable exception) implements RawResult {
}
