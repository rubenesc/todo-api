/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.resource;

import com.todo.api.resource.ext.PATCH;
import com.todo.api.domain.Todo;
import com.todo.api.exceptions.AppException;
import com.todo.api.filters.AppConst;
import com.todo.api.service.TodoService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ruben
 */
@Path("/v1/todo")
public class TodoResource {
   
    @Autowired
    private TodoService todoService;
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response create(Todo model) throws Exception {

        String id = todoService.create(model);
        // 201
        return Response.status(Response.Status.CREATED)
                .entity("created")
                .header("Location", AppConst.TODO_PATH + "/" + id).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> getItems() throws Exception {

        List<Todo> items = todoService.find();
        return items;

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Todo getItem(@PathParam("id") String id) throws Exception {

        Todo item = todoService.find(id);
        return item;

    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response putItem(@PathParam("id") String id, Todo model)
            throws AppException, Exception {

        Todo found = todoService.find(id);

        if (found == null) {

            String newId = todoService.create(model);
            // 201
            return Response.status(Response.Status.CREATED)
                    .entity("created")
                    .header("Location", AppConst.TODO_PATH + "/" + newId).build();

        } else {

            model.setId(id);
            todoService.update(model);
            // 200
            return Response.status(Response.Status.OK)
                    .entity("updated")
                    .header("Location", AppConst.TODO_PATH + "/" + id).build();

        }
    }
    
    
    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response patchItem(@PathParam("id") String id, Todo model)
            throws AppException, Exception {
        
        Todo found = todoService.find(id);

        if (found == null) {

            String newId = todoService.create(model);
            // 201
            return Response.status(Response.Status.CREATED)
                    .entity("created")
                    .header("Location", AppConst.TODO_PATH + "/" + newId).build();

        } else {

            model.setId(id);
            todoService.patch(model);
            // 200
            return Response.status(Response.Status.OK)
                    .entity("patched")
                    .header("Location", AppConst.TODO_PATH + "/" + id).build();

        }
    }    

    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_HTML})
    public Response deleteItem(@PathParam("id") String id) {

        todoService.delete(id);
        // 204
        return Response.status(Response.Status.NO_CONTENT)
                .entity("deleted").build();
    }

    public void setTodoService(TodoService todoService) {
        this.todoService = todoService;
    }
}
