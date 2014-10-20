package com.todo.api.exceptions.mappers;

import com.todo.api.exceptions.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handle all uncaught Exception, log them and return an Internal Server Error
 * 
 * 
 * @author ruben
 */
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    final static Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    public Response toResponse(Throwable ex) {
        
        //log exception
        logger.error(ex.getMessage(), ex);

        int status = getHttpStatus(ex);
        ErrorMessage errorMessage = new ErrorMessage(status, ex.getMessage());
        
        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private int getHttpStatus(Throwable ex) {

        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse().getStatus();
        } else {
            //defaults to internal server error 500            
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }
}
