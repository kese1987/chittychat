package com.example.commands;

public record Ping(String masterMessage) implements ExecutableCommands {
    public record Result(String response) {
    }
}
