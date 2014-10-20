package com.todo.api.exceptions;


import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.LoggerFactory;


/**
 * Generic HTTP error message
 * 
 * @author ruben
 */
public class ErrorMessage {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(ErrorMessage.class);
    
    int status; //HTTP Status code returned by the server

    String message; //error message

    public ErrorMessage() {
    }

    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public ErrorMessage(NotFoundException ex) {
        this.status = Response.Status.NOT_FOUND.getStatusCode();
        this.message = ex.getMessage();
    }
    
    public ErrorMessage(ValidationException ex) {
        this.status = Response.Status.BAD_REQUEST.getStatusCode();
        this.message = ex.getMessage();
    }
    
   public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
