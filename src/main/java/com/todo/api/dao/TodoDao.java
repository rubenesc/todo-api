/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.dao;

import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.ListOptions;
import java.util.List;

/**
 *
 * @author ruben
 */
public interface TodoDao {

    //create
    public void create(TodoEntity item);

    //retrieve
    public List<TodoEntity> find(ListOptions listOptions);

    public TodoEntity find(String id);

    //update
    public void update(TodoEntity item);
    
    //delete
    public int delete(String id);
    
    public int deleteAll();

    public long count();
    
}
