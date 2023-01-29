package com.example.commander;

import org.springframework.http.HttpStatus;

public class CommandException extends RuntimeException{
    private final Pod pod;

    private final HttpStatus httpStatus;
    private final Throwable e;

    public CommandException(Pod pod, HttpStatus httpStatus) {
        this(pod, httpStatus, null);
    }
    public CommandException(Pod pod, Throwable e) {
        this(pod, null, e);
    }
    private CommandException(Pod pod, HttpStatus httpStatus, Throwable e) {
        super("Unexpected pod reply");
        this.pod = pod;
        this.httpStatus = httpStatus;
        this.e = e;
    }

    public Pod getPod() {
        return pod;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Throwable getException() {
        return e;
    }
}

