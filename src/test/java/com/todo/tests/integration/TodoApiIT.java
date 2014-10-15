/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests.integration;

import com.todo.api.domain.Todo;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ruben
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:test-applicationContext.xml"})
public class TodoApiIT {

    private String BASE_URL = "http://localhost:8080";

 //   @Test
    public void testGetItem() throws JsonGenerationException,
            JsonMappingException, IOException {
        
        System.out.println("----> [testGetItem]");

        String id = "543e0e800364954c3a424c8f";

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client
                .target(BASE_URL + "/todo-app/todo/"+id);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        Todo item = response.readEntity(Todo.class);

        ObjectMapper mapper = new ObjectMapper();
        System.out.print(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(item));
    }    
    
    @Test
    public void testGetAllItems() throws JsonGenerationException,
            JsonMappingException, IOException {
        
        System.out.println("----> [testGetAllItems]");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client
                .target(BASE_URL + "/todo-app/todo");

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        List<Todo> items = response
                .readEntity(new GenericType<List<Todo>>() {
        });

        ObjectMapper mapper = new ObjectMapper();
        System.out.print(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(items));
        
    }
    
    
    
    public void postItem() throws Exception {
        
        System.out.println("----> [postItem]");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client
                .target(BASE_URL + "/todo-app/todo");

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        
        Todo entity = new Todo("Test Api", "Test rest methods of the TODO Api");
        
//        Response response = request.post(Cli)
//        Assert.assertTrue(response.getStatus() == 200);
        
        
    
    }
}
