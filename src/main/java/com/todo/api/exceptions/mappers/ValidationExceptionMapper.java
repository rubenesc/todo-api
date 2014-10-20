package com.todo.api.exceptions.mappers;

import com.todo.api.exceptions.ErrorMessage;
import com.todo.api.exceptions.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Handle request validation exceptions;
 * 
 * @author ruben
 */
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    final static Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);
    
    public Response toResponse(ValidationException ex) {
        
        //log exception
        logger.debug(ex.getMessage(), ex);
        
        ErrorMessage errorMessage = new ErrorMessage(
                Response.Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON) 
                .build();
        
        
    }
}
