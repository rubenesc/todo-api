/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import java.util.List;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.AppException;

/**
 *
 * @author ruben
 */
public interface TodoService {
    
    //create
    public String create(Todo item) throws Exception;
    
    //retrieve
    public List<Todo> find() throws Exception;

    public Todo find(String id) throws Exception;

    //update
    public void update(Todo item) throws Exception;
    
    //delete
    public void delete(String id);


}
