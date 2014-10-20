package com.todo.api.exceptions.mappers;

import com.todo.api.exceptions.ErrorMessage;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Handle request not found exceptions
 * 
 * @author ruben
 */
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    final static Logger logger = LoggerFactory.getLogger(NotFoundExceptionMapper.class);
    
    public Response toResponse(NotFoundException ex) {
        
        //log exception
        logger.debug(ex.getMessage(), ex);
        
        return Response.status(ex.getResponse().getStatus())
                .entity(new ErrorMessage(ex))
                .type(MediaType.APPLICATION_JSON)
                .build();
        
    }
}
