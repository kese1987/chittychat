package com.example.commander.raw.result;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @JsonSubTypes.Type(value = HandlerException.class, name = "e"),  @JsonSubTypes.Type(value = HandlerResult.class, name = "r")})
public sealed interface RawResult permits HandlerResult, HandlerException {
}
