package com.example.commander;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CommandException extends RuntimeException{
    private final Pod pod;
    private final List<DefaultRawResult> result;
    private final HttpStatus httpStatus;

    public CommandException(Pod pod, List<DefaultRawResult> result, HttpStatus httpStatus) {
        super("Unexpected reply from pod: " + pod.getName());
        this.pod = pod;
        this.result = result;
        this.httpStatus = httpStatus;
    }

    public Pod getPod() {
        return pod;
    }

    public List<DefaultRawResult> getResult() {
        return result;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
