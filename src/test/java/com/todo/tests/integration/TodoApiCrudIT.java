/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests.integration;

//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
import com.google.gson.Gson;
import com.todo.api.domain.Todo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
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
public class TodoApiCrudIT {

    private String BASE_URL = "http://localhost:8080";

    Client client;
    Todo model;

    @Before
    public void setUp() throws Exception {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        client = ClientBuilder.newClient(clientConfig);
        
        model = new Todo("Test API", "Test Todo API methods: POST, PUT, GET, DELETE");
    }

    @Test
    public void testTodoCrudOperations() throws Exception {
        
        testPost();     
        testUpdate();
        
        
    }
    
    private Todo testPost() throws Exception {
        
        String location = postItem(this.model);
        Todo found = getItemByLocation(location);
        
        Assert.assertNotNull(found);
        Assert.assertEquals(this.model.getTitle(), found.getTitle());
        Assert.assertEquals(this.model.getDescription(), found.getDescription());
        Assert.assertEquals(this.model.getDone(), found.getDone());
        
        this.model = found;
        
        return found;
    }    
    
    
    private void testUpdate() throws Exception {
        
        this.model.setTitle(this.model.getTitle() + " - updated through api");
        this.model.setDescription(this.model.getDescription() + " - updated through api");
        this.model.setDone(true);
        
        updateItem(this.model);
        Todo found = getItemById(this.model.getId());
        
        Assert.assertNotNull(found);
        Assert.assertEquals(this.model.getTitle(), found.getTitle());
        Assert.assertEquals(this.model.getDescription(), found.getDescription());
        Assert.assertEquals(this.model.getDone(), found.getDone());
    }        

    private String postItem(Todo item) {
        
        WebTarget webTarget = client.target(BASE_URL + "/todo-app/todo");

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);
        
        Response response = request.post(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 201);
        Assert.assertNotNull("Missing location on POST response", response.getLocation());
        
        return response.getLocation().toString();
        
    }
    
    private void updateItem(Todo item) {
        
        System.out.println("zzz" + BASE_URL + "/todo-app/todo/"+item.getId());
        WebTarget webTarget = client.target(BASE_URL + "/todo-app/todo/"+item.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);
        
        Response response = request.put(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 200);
        
    }    
    
    private Todo getItemById(String id) throws Exception {
    
        return getItemByLocation(BASE_URL + "/todo-app/todo" + id);
    }
    

    private Todo getItemByLocation(String location) throws Exception {

        WebTarget webTarget = client.target(location);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        Todo found = response.readEntity(Todo.class);

        ObjectMapper mapper = new ObjectMapper();
        System.out.print(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(found));
        
        return found;
    }


    
    
    
}
