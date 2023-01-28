package com.example.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Ping.class, name = "ping")})
public sealed interface ExecutableCommands permits Ping { }
