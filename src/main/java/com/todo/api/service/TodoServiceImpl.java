/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.AppException;
import com.todo.api.filters.AppConstants;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author ruben
 */
public class TodoServiceImpl implements TodoService {

    @Autowired
    TodoDao todoDao;

    public String create(Todo model) throws Exception {

        validateInputForCreation(model);

        TodoEntity entity = new TodoEntity(model);
        
        //persist data
        todoDao.create(entity);

        return entity.getId();
    }

    public void create(List<Todo> items) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Todo> find() throws Exception {

        List<TodoEntity> get = todoDao.find();
        List<Todo> todosFromEntities = getTodosFromEntities(get);
        return todosFromEntities;
    }

    public Todo find(String id) throws Exception {

        TodoEntity entity = todoDao.find(id);

        if (entity == null) {
            throw new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    "The item you requested with id " + id + " was not found.");
        }

        return new Todo(entity);

    }

    public void update(Todo item) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void delete(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTodoDao(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    //validation
    private void validateInputForCreation(Todo model) throws AppException {

        if (StringUtils.isEmpty(model.getTitle())) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), 400, "Insufficiente Data, title required");
        }
        
    }

    //Helper Methods
    private List<Todo> getTodosFromEntities(List<TodoEntity> entities) throws Exception {
        List<Todo> response = new ArrayList<Todo>();
        for (TodoEntity entity : entities) {
            response.add(new Todo(entity));
        }

        return response;
    }
}
