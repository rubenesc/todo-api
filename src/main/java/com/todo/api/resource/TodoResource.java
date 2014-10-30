/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.resource;

import com.todo.api.domain.ListOptions;
import com.todo.api.domain.ListWrapper;
import com.todo.api.resource.ext.PATCH;
import com.todo.api.domain.Todo;
import com.todo.api.filters.AppConst;
import com.todo.api.helpers.TodoHelper;
import com.todo.api.service.TodoService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.message.internal.HeaderValueException;
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
    public Response post(Todo model) throws Exception {
        
        Todo created = todoService.create(model);
        // 201
        return Response.status(Response.Status.CREATED)
                .entity("created")
                .header(AppConst.HEADER_LOCATION, AppConst.TODO_PATH + "/" + created.getId()).build();
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context UriInfo info) throws Exception {
        
        String p = info.getQueryParameters().getFirst("p");
        String l = info.getQueryParameters().getFirst("l");
        
        logger.info("Todo Resource get!!!");
        logger.info("Todo Resource get!!!");
        logger.info("Todo Resource get!!!");
        logger.info("Todo Resource get!!!");
        
        ListOptions opts = TodoHelper.buildListOptions(p, l);
        
        ListWrapper<Todo> wrapper = todoService.find(opts);

        //build response
        ResponseBuilder builder = null;
        builder = Response.ok(wrapper, MediaType.APPLICATION_JSON);
        return builder.build();
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id, @Context Request request) throws Exception {
        
        return find(id, request, true);
    }
    
    @HEAD
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response head(@PathParam("id") String id,
            @Context Request request) throws Exception {
        
        return find(id, request, false);
    }
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response update(@PathParam("id") String id, Todo model, @Context Request request)
            throws Exception {
        
        Todo found = todoService.find(id);
        
        if (found == null) {

            //create
            Todo created = todoService.create(model);
            // 201
            return Response.status(Response.Status.CREATED)
                    .entity("created")
                    .header(AppConst.HEADER_LOCATION, AppConst.TODO_PATH + "/" + created.getId()).build();
            
        } else {


            //if a conditional update was sent, validate it
            ResponseBuilder builder = validateConditionalUpdate(found, request);
            if (builder != null) {
                return builder.build();
            }            
            
            model.setId(id);
            todoService.update(model);
            // 200
            return Response.status(Response.Status.OK)
                    .entity("updated")
                    .header(AppConst.HEADER_LOCATION, AppConst.TODO_PATH + "/" + id).build();
            
        }
        
    }
    
    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_HTML})
    public Response patch(@PathParam("id") String id, Todo model, @Context Request request)
            throws Exception {
        
        Todo found = todoService.find(id);

        //if a conditional update was sent, validate it
        ResponseBuilder builder = validateConditionalUpdate(found, request);
        if (builder != null) {
            return builder.build();
        }        
        
        model.setId(id);
        todoService.partialUpdate(model);

        // 200
        return Response.status(Response.Status.OK)
                .entity("patched")
                .header(AppConst.HEADER_LOCATION, AppConst.TODO_PATH + "/" + id).build();
        
    }
    
    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_HTML})
    public Response delete(@PathParam("id") String id) throws Exception {
        
        todoService.delete(id);
        // 204
        return Response.status(Response.Status.NO_CONTENT)
                .entity("deleted").build();
        
    }
    
    @GET
    @Path("{id}/done")
    @Produces(MediaType.APPLICATION_JSON)
    public Response done(@PathParam("id") String id) throws Exception {
        
        return updateDoneStatus(id, true);
    }
    
    @GET
    @Path("{id}/undone")
    @Produces(MediaType.APPLICATION_JSON)
    public Response undone(@PathParam("id") String id) throws Exception {
        
        return updateDoneStatus(id, false);
    }

    /**
     *
     * Implements GET and HEAD
     *
     * @param id
     * @param request
     * @param returnBody
     * @return
     * @throws Exception
     */
    private Response find(String id, Request request, boolean returnBody) throws Exception {
        
        Todo item = todoService.find(id);
        
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(1000);

        //if a conditional GET was sent, validate it
        ResponseBuilder builder = validateConditionalGet(item, request);
        if (builder != null) {
            builder.cacheControl(cacheControl); // reset the client’s cache expiration
            return builder.build();
        }

        //200 build response
        builder = (returnBody) ? Response.ok(item, MediaType.APPLICATION_JSON)
                : Response.ok();
        
        builder.cacheControl(cacheControl);
        builder.tag(new EntityTag(Integer.toString(item.hashCode())));        
        return builder.build();
    }
    
    private Response updateDoneStatus(String id, boolean done) throws Exception {
        
        Todo model = new Todo();
        model.setId(id);
        model.setDone(done);
        
        todoService.partialUpdate(model);

        // 200
        return Response.status(Response.Status.OK)
                .entity("updated")
                .header(AppConst.HEADER_LOCATION, AppConst.TODO_PATH + "/" + id).build();
    }
    
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context UriInfo info) throws Exception {
        
        String query = info.getQueryParameters().getFirst("q");
        
        List<Todo> items = todoService.search(query);

        //build response
        ResponseBuilder builder = null;
        builder = Response.ok(items, MediaType.APPLICATION_JSON);
        return builder.build();
    }

    /**
     * Conditional GET
     *
     * Validates if an 'If-None-Match' header comes in the request, then it's
     * value must match the value of the current resource eTag.
     *
     * Example Request: GET /v1/todo/123 HTTP/1.1 If-None-Match:
     * "81422713435543276343221"
     *
     * @param Todo found in the database
     * @param request
     *
     * @return null: Preconditions met.
     * @return ResponseBuilder: Preconditions not met.
     */
    private ResponseBuilder validateConditionalGet(Todo item, Request request) {
        
        EntityTag eTag = new EntityTag(Integer.toString(item.hashCode()));
        
        try {

            //verify request contains an If-None-Match header with a valid ETAG
            ResponseBuilder builder = request.evaluatePreconditions(eTag);
            if (builder != null) {
                //304, Found valid ETag, Resource has not changed.
                return builder;
            }            
            
        } catch (HeaderValueException e) {
            //HeaderValueException: Unable to parse "If-None-Match" header value ...
            logger.debug(e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Conditional update
     *
     * Validates if an 'If-Match' header comes in the request, then it's value
     * must match the value of the eTag of the resource we want to update. This
     * will assure we are not working with stale data.
     *
     * Example Request: PUT /v1/todo HTTP/1.1 If-Match:
     * "81422713435543276343221"
     *
     * @param Todo found in the database
     * @param request
     *
     * @return null: Preconditions met.
     * @return ResponseBuilder: Preconditions not met.
     */
    private ResponseBuilder validateConditionalUpdate(Todo item, Request request) {

        //get the ETag of the resource we want to update.
        EntityTag updateETag = new EntityTag(Integer.toString(item.hashCode()));
        
        try {

            //verify if request contains an If-Match header with with an eTag value,
            //and that it matches the value of 'updateEtag'
            ResponseBuilder builder = request.evaluatePreconditions(updateETag);
            
            if (builder != null) {
                // Preconditions not met! (ETag and If-Match)
                // The updateETag doesn't match the eTag sent in the If-Match Header
                return builder; //412 Precondition Failed
            }
            
            
        } catch (HeaderValueException e) {
            //For some reason, if etag doesn't exist it throws a 
            //HeaderValueException: Unable to parse "If-Match" header value ...
            logger.debug(e.getMessage(), e);
            return Response.status(Response.Status.PRECONDITION_FAILED);
        }
        
        
        return null; //Preconditions met
    }

    //spring DI
    public void setTodoService(TodoService todoService) {
        this.todoService = todoService;
    }    
}
