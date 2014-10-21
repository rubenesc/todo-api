/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.domain;

import com.todo.api.dao.model.TodoEntity;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author ruben
 */
public class Todo {
    
    private String id;
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

    public Todo(TodoEntity entity) throws Exception {
        BeanUtils.copyProperties(this, entity);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Todo{" + "id=" + id + ", title=" + title + ", description=" + description + ", done=" + done + '}';
    }
    
}
