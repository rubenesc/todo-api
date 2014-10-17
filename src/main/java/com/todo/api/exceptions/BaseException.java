/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.exceptions;

import java.io.Serializable;

/**
 *
 * @author ruben.escudero
 */
public class BaseException extends Exception implements Serializable {

    private String displayMessage;    
    
    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Throwable ex) {
        super(ex);
    }

    public BaseException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public BaseException(String mensaje, Throwable causa, String displayMessage) {
        super(mensaje, causa);
        this.displayMessage= displayMessage;
    }
    
    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }    
    
}
