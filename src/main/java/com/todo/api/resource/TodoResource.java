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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ruben
 */
@Path("/v1/todo")
public class TodoResource {

    final static Logger logger = LoggerFactory.getLogger(TodoResource.class);
    @Autowired
    private TodoService todoService;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response create(Todo model) throws Exception {
        
        try {

            Todo created = todoService.create(model);
            // 201
            return Response.status(Response.Status.CREATED)
                    .entity("created")
                    .header("Location", AppConst.TODO_PATH + "/" + created.getId()).build();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> getItems() throws Exception {
        
        try {
            List<Todo> items = todoService.find();
            return items;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> search(@QueryParam("q") String query) throws Exception {
        
        try {
            List<Todo> items = todoService.search(query);
            return items;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Todo getItem(@PathParam("id") String id) throws Exception {
        
        try {
            Todo item = todoService.find(id);
            return item;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @GET
    @Path("{id}/done")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markDone(@PathParam("id") String id) throws Exception {
        
        try {
            Todo model = new Todo();
            model.setId(id);
            model.setDone(true);

            todoService.partialUpdate(model);

            // 200
            return Response.status(Response.Status.OK)
                    .entity("updated")
                    .header("Location", AppConst.TODO_PATH + "/" + id).build();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @GET
    @Path("{id}/undone")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markUndone(@PathParam("id") String id) throws Exception {
        
        try {
            Todo model = new Todo();
            model.setId(id);
            model.setDone(false);

            todoService.partialUpdate(model);

            // 200
            return Response.status(Response.Status.OK)
                    .entity("updated")
                    .header("Location", AppConst.TODO_PATH + "/" + id).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response update(@PathParam("id") String id, Todo model)
            throws AppException, Exception {
        
        try {

            Todo found = todoService.find(id);

            if (found == null) {

                Todo created = todoService.create(model);
                // 201
                return Response.status(Response.Status.CREATED)
                        .entity("created")
                        .header("Location", AppConst.TODO_PATH + "/" + created.getId()).build();

            } else {

                model.setId(id);
                todoService.update(model);
                // 200
                return Response.status(Response.Status.OK)
                        .entity("updated")
                        .header("Location", AppConst.TODO_PATH + "/" + id).build();

            }
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response patch(@PathParam("id") String id, Todo model)
            throws AppException, Exception {
        
        try {
            
            model.setId(id);
            todoService.partialUpdate(model);
            
            // 200
            return Response.status(Response.Status.OK)
                    .entity("patched")
                    .header("Location", AppConst.TODO_PATH + "/" + id).build();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_HTML})
    public Response delete(@PathParam("id") String id) throws Exception {
        
        try {
            todoService.delete(id);
            // 204
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("deleted").build();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    //spring DI
    public void setTodoService(TodoService todoService) {
        this.todoService = todoService;
    }
}
