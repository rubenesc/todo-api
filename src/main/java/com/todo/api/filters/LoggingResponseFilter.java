package com.todo.api.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingResponseFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingResponseFilter.class);

    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {
        
        String method = requestContext.getMethod();

        logger.debug("Requesting " + method + " for path " + requestContext.getUriInfo().getPath());
        
        Object entity = responseContext.getEntity();
        String response = null;
        if (entity != null) {

            response = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entity);            
            
            logger.debug("Response " + response);
        }
        
//        for (int i = 0; i < 5; i++){
//            System.out.println("["+method+"][]["+requestContext.getUriInfo().getPath()+"]["+requestContext.getUriInfo().getPathParameters()+"]["+response+"]");
//        }

    }
}