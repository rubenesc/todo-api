/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.ListOptions;
import com.todo.api.domain.ListWrapper;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.ValidationException;
import com.todo.api.helpers.TodoHelper;
import com.twilio.sdk.TwilioRestException;
import java.util.List;
import javax.ws.rs.NotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author ruben
 */
@Service
public class TodoService {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TodoService.class);
    
    @Autowired
    TodoDao todoDao;
    
    @Autowired
    SearchService searchService;    
    
    @Autowired
    SmsService smsService;  
        
    public Todo create(Todo model) throws Exception {
        
        validateInputForCreation(model);
        
        //update attributes
        TodoEntity entity = new TodoEntity();
        updateAttributes(entity, model);
        
        //persist data
        todoDao.create(entity);
        
        //index data
        searchService.index(entity);
        
        return new Todo(entity);
    }


    public ListWrapper<Todo> find(ListOptions opts) throws Exception {

        TodoHelper.validateListOptions(opts);
        
        List<TodoEntity> entities = todoDao.find(opts);

        long count = todoDao.count();
        
        List<Todo> models = TodoHelper.convertEntitiesToModels(entities);
        
        ListWrapper<Todo> listWrapper = new ListWrapper<Todo>();
        listWrapper.setItems(models);
        listWrapper.setPage(opts.getPage());
        listWrapper.setLimit(opts.getLimit());
        listWrapper.setTotal(count);
        
        return listWrapper;
        
    }

    public Todo find(String id) throws Exception {
    
        TodoEntity entity = todoDao.find(id);

        validateFound(entity, id);
        
        return new Todo(entity);

    }

    public void update(Todo item) throws Exception {
        
        validateInputForUpdate(item);
        
        TodoEntity entity = todoDao.find(item.getId());

        validateFound(entity, item.getId());
        
        boolean before = entity.getDone();
        
        updateAttributes(entity, item);
        
        todoDao.update(entity);
        
        isTaskDone(entity, before);
        
    }
    
    public void partialUpdate(Todo item) throws Exception {
        
        TodoEntity entity = todoDao.find(item.getId());

        validateFound(entity, item.getId());
        
        boolean before = entity.getDone();
        
        patchAttributes(entity, item);
        
        validateInputForPatch(entity);
        
        todoDao.update(entity);
        
        isTaskDone(entity, before);
        
    }    

    public void delete(String id) {
        
        //delete from DB
        todoDao.delete(id);
        
        //delete index
        searchService.deleteDocument(id);
        
    }
    
    public List<Todo> search(String query) throws Exception {
        
            List<TodoEntity> entities = searchService.searchTodos(query);

            List<Todo> models = TodoHelper.convertEntitiesToModels(entities);
            return models;
        
    }    
    
    

    //validation
    private void validateInputForCreation(Todo model) throws ValidationException {

        if (StringUtils.isEmpty(model.getTitle())) {
            throw new ValidationException("Insufficiente Data, title required");
        }

    }
    
    private void validateInputForUpdate(Todo model) throws ValidationException {

        if (StringUtils.isEmpty(model.getTitle())) {
            throw new ValidationException("Insufficiente Data, title required");
        }

    }
    
    private void validateInputForPatch(TodoEntity entity) throws ValidationException {

        if (StringUtils.isEmpty(entity.getTitle())) {
            throw new ValidationException("Insufficiente Data, title required");
        }

    }    
    
    //Helper Methods


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
    
    
    /**
     * Verifies if the Todo's done state changed from false to true,
     * if so send an sms msg.
     * 
     * @param entity: contains the values after the update
     * @param before: value of done before update
     * @throws ValidationException
     * @throws TwilioRestException 
     */
    private void isTaskDone(TodoEntity entity, boolean before) throws ValidationException, TwilioRestException {
        
        if (entity.getDone() && (!before)){
            String msg = "Todo Completed: " + entity.getTitle();
            this.smsService.send(msg);
        }
    }

    private void validateFound(TodoEntity entity, String id) throws NotFoundException {
        
        if (entity == null) {
            throw new NotFoundException("The resource with id " + id + " was not found.");
        }
    }

    
    //Spring DI 
    public void setTodoDao(TodoDao todoDao) {
        this.todoDao = todoDao;
    }
    
}
