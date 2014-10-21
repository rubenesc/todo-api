/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.helpers;

import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.ListOptions;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.ValidationException;
import com.todo.api.filters.AppConst;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

/**
 *
 * @author ruben
 */
public class TodoHelper {

    public static List<Todo> convertEntitiesToModels(List<TodoEntity> entities) throws Exception {
        List<Todo> response = new ArrayList<Todo>();
        for (TodoEntity entity : entities) {
            response.add(new Todo(entity));
        }

        return response;
    }
    
    public static ListOptions buildListOptions(String p, String l) throws ValidationException {
        
        Integer page = null;
        Integer limit = null;
        
        if (!StringUtils.isEmpty(p)) {
            try {
                page = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid page attribute");
            }
        }
        
        if (!StringUtils.isEmpty(l)) {
            try {
                limit = Integer.parseInt(l);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid limit attribute");
            }
        }
        
        return new ListOptions(page, limit);
    }    
    
    public static void validateListOptions(ListOptions listOptions) {
        //page size
        if (listOptions.getLimit() != null && Math.abs(listOptions.getLimit()) < 100){
            listOptions.setLimit(Math.abs(listOptions.getLimit()));
        } else { 
            listOptions.setLimit(AppConst.PAG_DEFAULT_LIMIT);
        }
        
        //page
        if (listOptions.getPage() != null){
            listOptions.setPage(Math.abs(listOptions.getPage()));
        } else { 
            listOptions.setPage(1);
        }
    }
    
}
