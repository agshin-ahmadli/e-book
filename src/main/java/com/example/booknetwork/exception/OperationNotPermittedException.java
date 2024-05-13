package com.example.booknetwork.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String msg) {
        super(msg);
    }
}
