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

        // ex: http://localhost:8080/todo-app/v1/todo
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
        testPatchDone();
        testDelete();

        //create multiple documents
        testPostMultiple();
        testGetAll();

        //test search
        testSearch();

        //test mark done/undone 
        testStatus();
        
    }

    private void testStatus() throws Exception {

        //create new item
        Todo item = new Todo("Send Sms message", "Once completed send sms message", false);
        item = testPost(item);

        //mark as done.
        this.markDone(item.getId());

        //verify
        item = this.getById(item.getId());
        Assert.assertTrue(item.getDone());
        
        //mark as undone.
        this.markUndone(item.getId());

        //verify
        item = this.getById(item.getId());
        Assert.assertFalse(item.getDone());

    }

    private List<Todo> testSearch() throws Exception {

        String query = "2";
        WebTarget webTarget = client.target(TODO_API_URL + "/search?q=" + query);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        List<Todo> items = response.readEntity(new GenericType<List<Todo>>() {
        });

        System.out.println("testSearch results [" + items.size() + "]");
        for (Todo item : items) {
            System.out.println(item);
        }

        return items;
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

        Todo found = this.testPost(this.model);
        this.model = found;
        return found;

    }

    private Todo testPost(Todo model) throws Exception {

        String location = post(model);
        Todo found = getByLocation(location);

        Assert.assertNotNull(found);
        Assert.assertEquals(model.getTitle(), found.getTitle());
        Assert.assertEquals(model.getDescription(), found.getDescription());
        Assert.assertEquals(model.getDone(), found.getDone());

        return found;
    }

    private Todo testUpdate() throws Exception {

        this.model.setTitle(this.model.getTitle() + " - updated through api");
        this.model.setDescription(this.model.getDescription() + " - updated through api");
        this.model.setDone(!this.model.getDone());

        return testUpdate(this.model);

    }

    private Todo testUpdate(Todo model) throws Exception {

        update(model);
        Todo found = getById(model.getId());

        Assert.assertNotNull(found);
        Assert.assertEquals(model.getTitle(), found.getTitle());
        Assert.assertEquals(model.getDescription(), found.getDescription());
        Assert.assertEquals(model.getDone(), found.getDone());

        return found;
    }

    private Todo testPatchDone() throws Exception {

        boolean done = !this.model.getDone();

        Todo todoPatch = new Todo();
        todoPatch.setId(this.model.getId());
        todoPatch.setDone(done);

        return testPatchDone(todoPatch, this.model);
    }

    private Todo testPatchDone(Todo todoPatch, Todo todo) throws Exception {


        patch(todoPatch);
        Todo found = getById(todo.getId());

        Assert.assertNotNull(found);
        Assert.assertEquals(todo.getTitle(), found.getTitle());
        Assert.assertEquals(todo.getDescription(), found.getDescription());
        Assert.assertEquals(todoPatch.getDone(), found.getDone());

        return found;
    }

    private void testDelete() throws Exception {
        String id = this.model.getId();
        testDelete(id);
    }

    private void testDelete(String id) throws Exception {

        Todo found = getById(id);

        delete(found);

        //make sure it doesn't exist anymore
        WebTarget webTarget = client.target(TODO_API_URL + "/" + found.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 404);
    }

    private void testPostMultiple() {

        for (int i = 1; i <= this.numCreate; i++) {

            post(new Todo("Test Todo #" + i, "Todo Description #" + i));

        }
    }

    //Basic operations, POST, PUT, PATCH, GET, DELETE
    private String post(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.post(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 201);
        Assert.assertNotNull("Missing location on POST response", response.getLocation());

        return response.getLocation().toString();

    }

    private void update(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.put(Entity.entity(item, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == 200);

    }

    /**
     * Partial update with PATCH method. Used Apache httpClient instead of
     * jersey, due to lack of support of PATCH method.
     *
     * @throws Exception
     */
    public void patch(Todo item) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPatch httpPatch = new HttpPatch(TODO_API_URL + "/" + item.getId());

        Gson gson = new Gson();
        StringEntity entity = new StringEntity(gson.toJson(item));
        entity.setContentType(MediaType.APPLICATION_JSON);

        httpPatch.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPatch);

        Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
    }

    private void delete(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.delete();
        Assert.assertTrue(response.getStatus() == 204);

    }

    private void markDone(String id) throws Exception {

        String location = TODO_API_URL + "/" + id + "/done";
        
        WebTarget webTarget = client.target(location);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);
        
    }

    private void markUndone(String id) throws Exception {

        String location = TODO_API_URL + "/" + id + "/undone";
        
        WebTarget webTarget = client.target(location);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

    }

    private Todo getById(String id) throws Exception {

        return getByLocation(TODO_API_URL + "/" + id);
    }

    private Todo getByLocation(String location) throws Exception {

        WebTarget webTarget = client.target(location);

        Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        Assert.assertTrue(response.getStatus() == 200);

        Todo found = response.readEntity(Todo.class);

        return found;
    }
}
