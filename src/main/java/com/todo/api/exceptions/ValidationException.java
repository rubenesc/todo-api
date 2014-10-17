package com.todo.api.exceptions;

public class ValidationException extends BaseException {

    public ValidationException() {
    }

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(Throwable ex) {
        super(ex);
    }

    public ValidationException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public ValidationException(String msg, Throwable ex, String displayMessage) {
        super(msg, ex, displayMessage);
    }
}