/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.config;

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
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author ruben
 */
public class JerseyConfig extends ResourceConfig {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JerseyConfig.class);

    /**
     * Register JAX-RS application components.
     */
    public JerseyConfig() {

        // register application resources
        register(TodoResource.class);

        registerFilters();
        registerExceptionMappers();
        registerFeatures();
        
        printAppConfig();
        
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

    private void printAppConfig() {
        
        logger.info("App Config:");
        Environment env = ContextLoader.getCurrentWebApplicationContext().getEnvironment();
        
        logger.info("Active Profile:");
        String[] activeProfiles = env.getActiveProfiles();
        for (int i = 0; i < activeProfiles.length; i++) {
            logger.info("["+activeProfiles[i]+"]");
        }
        
        logger.info("Default Profile:");
         activeProfiles = env.getDefaultProfiles();
        for (int i = 0; i < activeProfiles.length; i++) {
            logger.info("["+activeProfiles[i]+"]");
        }
        
    }
}
