package com.example.commander.raw.result;

public record HandlerException(Throwable e) implements RawResult{
}
