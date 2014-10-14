/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api;

import com.todo.domain.Todo;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ruben
 */
@Path("/todo")
public class TodoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> getUsers() {
        
        List<Todo> items = new ArrayList<Todo>();
        Todo item = new Todo("work", "finish API");
        items.add(item);
        return items;

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Todo getUser() {
        
        Todo item = new Todo("work", "finish  API");
        return item;

    }
    
    
}
