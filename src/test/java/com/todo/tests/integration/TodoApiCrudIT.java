/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests.integration;

//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
import com.google.gson.Gson;
import com.todo.api.dao.TodoDao;
import com.todo.api.domain.Todo;
import com.todo.api.filters.AppConst;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.junit.Before;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    private String TODO_API_URL;
    Client client;
    Todo model;
    int numCreate; //Numer of documents to create
    @Autowired
    TodoDao todoDao;

    @Before
    public void setUp() throws Exception {

        TODO_API_URL = BASE_URL + AppConst.TODO_PATH;
        numCreate = 25;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        client = ClientBuilder.newClient(clientConfig);

        model = new Todo("Test API", "Test Todo API methods: POST, PUT, GET, DELETE");

        todoDao.deleteAll();
    }

    @Test
    public void testTodoCrudOperations() throws Exception {

        testGetAll();
        testPost();
        testUpdate();
        testPatch();
        testDelete();

        //create multiple documents
        testPostMultiple();
        testGetAll();

    }

    private List<Todo> testGetAll() throws Exception {


        WebTarget webTarget = client.target(TODO_API_URL);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        List<Todo> items = response.readEntity(new GenericType<List<Todo>>() {
        });

        return items;
    }

    private Todo testPost() throws Exception {

        String location = postItem(this.model);
        Todo found = findByLocation(location);

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
        Todo found = findById(this.model.getId());

        Assert.assertNotNull(found);
        Assert.assertEquals(this.model.getTitle(), found.getTitle());
        Assert.assertEquals(this.model.getDescription(), found.getDescription());
        Assert.assertEquals(this.model.getDone(), found.getDone());
    }

    private void testPatch() throws Exception {

        boolean done = !this.model.getDone();

        Todo todoPatch = new Todo();
        todoPatch.setId(this.model.getId());
        todoPatch.setDone(done);

        patchItem(todoPatch);
        Todo found = findById(this.model.getId());

        Assert.assertNotNull(found);
        Assert.assertEquals(this.model.getTitle(), found.getTitle());
        Assert.assertEquals(this.model.getDescription(), found.getDescription());
        Assert.assertEquals(todoPatch.getDone(), found.getDone());
    }

    private void testDelete() throws Exception {

        Todo found = findById(this.model.getId());

        deleteItem(found);

        //make sure it doesn't exist anymore
        WebTarget webTarget = client.target(TODO_API_URL + "/" + found.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 404);
    }

    private void testPostMultiple() {

        for (int i = 1; i <= this.numCreate; i++) {

            postItem(new Todo("Test Todo #" + 1, "Descripion #" + 1));

        }
    }

    private String postItem(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.post(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 201);
        Assert.assertNotNull("Missing location on POST response", response.getLocation());

        return response.getLocation().toString();

    }

    private void updateItem(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.put(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 200);

    }
    
    
    /**
     * Partial update with PATCH method.
     * Used Apache httpClient instead of jersey, due to lack of support
     * of PATCH method.
     * 
     * @throws Exception 
     */
    public void patchItem(Todo item) throws Exception {
        
            HttpClient httpClient = new DefaultHttpClient();
            HttpPatch httpPatch = new HttpPatch(TODO_API_URL + "/" + item.getId());

            Gson gson = new Gson();
            StringEntity entity = new StringEntity(gson.toJson(item));
            entity.setContentType(MediaType.APPLICATION_JSON);
            
            httpPatch.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPatch);
            
            Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
    }

    private void deleteItem(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.delete();
        Assert.assertTrue(response.getStatus() == 204);

    }

    private Todo findById(String id) throws Exception {

        return findByLocation(TODO_API_URL + "/" + id);
    }

    private Todo findByLocation(String location) throws Exception {

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
