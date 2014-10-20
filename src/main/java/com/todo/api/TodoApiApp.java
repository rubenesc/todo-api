/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api;

import com.todo.api.exceptions.mappers.GenericExceptionMapper;
import com.todo.api.exceptions.mappers.NotFoundExceptionMapper;
import com.todo.api.exceptions.mappers.ValidationExceptionMapper;
import com.todo.api.filters.CORSResponseFilter;
import com.todo.api.filters.LoggingResponseFilter;
import com.todo.api.resource.TodoResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 *
 * @author ruben
 */
public class TodoApiApp extends ResourceConfig {

    /**
     * Register JAX-RS application components.
     */
    public TodoApiApp() {

        // register application resources
        register(TodoResource.class);
        
        registerFilters();
        registerExceptionMappers();
        registerFeatures();

    }
    
    // register exception mappers
    private void registerExceptionMappers() {
        register(GenericExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
        register(ValidationExceptionMapper.class);
    }

    // register filters
    private void registerFilters() {
        register(RequestContextFilter.class);
        register(LoggingResponseFilter.class);
        register(CORSResponseFilter.class);
    }

    // register features
    private void registerFeatures() {
        register(JacksonFeature.class);
        register(MultiPartFeature.class);
        register(EntityFilteringFeature.class);
    }
}
