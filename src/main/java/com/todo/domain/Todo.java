/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.domain;

/**
 *
 * @author ruben
 */
public class Todo {
    
    private String title;
    private String description;
    private Boolean done = false;

    public Todo() {
    }

    public Todo(String title) {
        this.title = title;
    }
    
    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Todo(String title, String description, Boolean done) {
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
    
}
