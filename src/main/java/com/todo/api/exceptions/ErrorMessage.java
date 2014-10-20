package com.todo.api.exceptions;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.beanutils.BeanUtils;

@XmlRootElement
public class ErrorMessage {

    /**
     * contains the same HTTP Status code returned by the server
     */
    @XmlElement(name = "status")
    int status;

    /**
     * message describing the error
     */
    @XmlElement(name = "message")
    String message;


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


    public ErrorMessage(AppException ex) {
        try {
            BeanUtils.copyProperties(this, ex);
        } catch (Exception ex1) {
            Logger.getLogger(ErrorMessage.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    public ErrorMessage(NotFoundException ex) {
        this.status = Response.Status.NOT_FOUND.getStatusCode();
        this.message = ex.getMessage();
    }

    public ErrorMessage() {
    }
}
