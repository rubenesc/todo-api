package com.todo.api.exceptions;

public class AppException extends Exception {

    Integer status; //HTTP status code
    int code;

    /**
     *
     * @param status
     * @param code
     * @param message
     * @param developerMessage
     */
    public AppException(int status, int code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public AppException() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
