/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.AppException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author ruben
 */
public class TodoServiceImpl implements TodoService {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);
    
    @Autowired
    TodoDao todoDao;
    
    @Autowired
    SearchService searchService;    

    public String create(Todo model) throws Exception {

        validateInputForCreation(model);

        TodoEntity entity = new TodoEntity(model);

        //persist data
        todoDao.create(entity);
        
        //index data
        searchService.index(entity);
        
        return entity.getId();
    }

    public void create(List<Todo> items) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Todo> find() throws Exception {
        
        List<TodoEntity> entities = todoDao.find();
        List<Todo> models = convertEntitiesToModels(entities);
        return models;
        
    }

    public Todo find(String id) throws Exception {

        TodoEntity entity = todoDao.find(id);

        if (entity == null) {
            throw new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404, "The item you requested with id " + id + " was not found.");
        }

        return new Todo(entity);

    }

    public void update(Todo item) throws Exception {
        
        validateInputForUpdate(item);
        
        TodoEntity entity = todoDao.find(item.getId());
        
        updateAttributes(entity, item);
        
        todoDao.update(entity);
        
    }
    
    public void patch(Todo item) throws Exception {
        
        TodoEntity entity = todoDao.find(item.getId());
        
        patchAttributes(entity, item);
        
        validateInputForPatch(entity);
        
        todoDao.update(entity);
        
    }    

    public void delete(String id) {
        
        //delete from DB
        todoDao.delete(id);
        
        //delete index
        searchService.deleteDocument(id);
        
    }
    
    

    public List<Todo> search(String query) throws Exception {
        
            List<TodoEntity> entities = searchService.searchTodos(query);

            List<Todo> models = convertEntitiesToModels(entities);
            return models;
        
    }    
    
    
    //Spring DI 
    public void setTodoDao(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    //validation
    private void validateInputForCreation(Todo model) throws AppException {

        if (StringUtils.isEmpty(model.getTitle())) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), 400, "Insufficiente Data, title required");
        }

    }
    
    private void validateInputForUpdate(Todo model) throws AppException {

        if (StringUtils.isEmpty(model.getTitle())) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), 400, "Insufficiente Data, title required");
        }

    }
    
    private void validateInputForPatch(TodoEntity entity) throws AppException {

        if (StringUtils.isEmpty(entity.getTitle())) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), 400, "Insufficiente Data, title required");
        }

    }    
    
    //Helper Methods
    private List<Todo> convertEntitiesToModels(List<TodoEntity> entities) throws Exception {
        List<Todo> response = new ArrayList<Todo>();
        for (TodoEntity entity : entities) {
            response.add(new Todo(entity));
        }

        return response;
    }

    private void updateAttributes(TodoEntity entity, Todo model) {
        entity.setTitle(model.getTitle());
        entity.setDescription(model.getDescription());
        entity.setDone(model.getDone());
    }
    
    /*
     * update attributes that are not empty
     */
    private void patchAttributes(TodoEntity entity, Todo model) {
        
        if (!StringUtils.isEmpty(model.getTitle())){
            entity.setTitle(model.getTitle());
        }
        
        if (!StringUtils.isEmpty(model.getDescription())){
            entity.setDescription(model.getDescription());
        }
        
        if (!StringUtils.isEmpty(model.getDone())){
            entity.setDone(model.getDone());
        }
        
    }
}
