/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests.integration;

//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
import com.todo.tests.integration.ext.RestOperations;
import com.todo.api.dao.TodoDao;
import com.todo.api.domain.ListWrapper;
import com.todo.api.domain.Todo;
import com.todo.api.filters.AppConst;
import java.util.List;
import org.junit.Before;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ruben
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:test-applicationContext.xml"})
public class TodoApiCrudIT extends RestOperations{

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TodoApiCrudIT.class);
    
    Todo model;
    int numCreate; //Numer of documents to create
    
    @Autowired
    TodoDao todoDao;

    @Before
    public void setUp() throws Exception {

        init();

        numCreate = 25;
        model = new Todo("Test API", "Test Todo API methods: POST, PUT, GET, DELETE");
        todoDao.deleteAll();
    }

    @Test
    public void tests() throws Exception {

        testGetAll(AppConst.PAG_DEFAULT_LIMIT);
        
        testPost();
        testInvalidPost();
        
        testUpdate();
        testInvalidUpdate();
        
        testPatchDone();
        testInvalidPatch();
        
        testDelete();

        //create multiple documents
        testPostMultiple();
        testGetAll(5);

        //test search
        testSearch();

        //test mark done/undone 
        testStatus();
        testInvalidStatus();
        
        testBadRequest();
    }

    private void testBadRequest(){
        
        //create empty todo
        Todo item = new Todo();
        Response response = post(item);
        
        //expect 400 - Malformed message
        Assert.assertEquals(400, response.getStatus());
        
    }
    
    
    private void testInvalidStatus() throws Exception {
        String id = "A1B2C3D4E5F6G7";
        Response response = this.markDone(id);
        Assert.assertEquals(404, response.getStatus()); //Not found
    }
        
    
    private void testStatus() throws Exception {

        //create new item
        Todo item = new Todo("Send Sms message", "Once completed send sms message", false);
        item = testPost(item);

        //mark as done.
        Response response = this.markDone(item.getId());
        Assert.assertEquals(200, response.getStatus());

        //verify
        item = this.getById(item.getId());
        Assert.assertTrue(item.getDone());
        
        //mark as undone.
        response = this.markUndone(item.getId());
        Assert.assertEquals(200, response.getStatus());

        //verify
        item = this.getById(item.getId());
        Assert.assertFalse(item.getDone());

    }

    private List<Todo> testSearch() throws Exception {

        String query = "2";
        Response response = get(TODO_API_URL + "/search?q=" + query);
        
        Assert.assertEquals(200, response.getStatus());
        
        List<Todo> items = response.readEntity(new GenericType<List<Todo>>() {
        });

        return items;
    }

    private void testGetAll(int pageLimit) throws Exception {
        
        int counter = 0;
        long totalDocuments = todoDao.count();        
        int totalPages = (int) Math.ceil((double)totalDocuments / pageLimit);
        
        for (int page = 1; page <= totalPages; page++) {
            
            
            Response response = get(TODO_API_URL+"?p="+page+"&l="+pageLimit);
            Assert.assertEquals(200, response.getStatus());
            ListWrapper<Todo> listWrapper = response.readEntity(new GenericType<ListWrapper<Todo>>() {
            });            
            
            Assert.assertEquals(totalDocuments, listWrapper.getTotal());

            List<Todo> items = listWrapper.getItems();
            
            for (Todo item : items) {
                logger.info("item [" + item + "]");
                counter++;
            }
            
        }

        Assert.assertEquals("Did not paginate all items", totalDocuments, counter);
        
        
        

        
    }

    private void testInvalidPost() {
        
        Todo item = new Todo();
        Response response = post(item);

        Assert.assertEquals(400, response.getStatus()); //Bad request
    }
    
    
    private Todo testPost() throws Exception {

        Todo found = this.testPost(this.model);
        this.model = found;
        
        return found;
    }
    
    private Todo testPost(Todo model) throws Exception {
        
        //create item
        Response responsePost = post(model);
        Assert.assertEquals(201, responsePost.getStatus());
        Assert.assertNotNull("Missing location on POST response", responsePost.getLocation());
//        Todo created = responsePost.readEntity(Todo.class);
//        verifyItemMatch(model, created);        
        
        String location = responsePost.getLocation().toString();
        
        //find it and verify
        Response responseGet = get(location);
        Assert.assertEquals(200, responseGet.getStatus());
        Todo foundGet = responseGet.readEntity(Todo.class);
        verifyItemMatch(model, foundGet);

        return foundGet;
    }
    
    private void testInvalidUpdate() {
        
        Todo item = new Todo("some title", "some description", true);
        item.setId("123456");
        
        Response response = update(item);
        Assert.assertEquals(404, response.getStatus()); //Not found
    }
    

    private Todo testUpdate() throws Exception {

        this.model.setTitle(this.model.getTitle() + " - updated through api");
        this.model.setDescription(this.model.getDescription() + " - updated through api");
        this.model.setDone(!this.model.getDone());

        return testUpdate(this.model);

    }

    private Todo testUpdate(Todo model) throws Exception {
        
        //update item
        Response response = update(model);
        Assert.assertEquals(200, response.getStatus());

        //verify update
        Todo found = getById(model.getId());
        verifyItemMatch(model, found);

        return found;
    }
    
    private void testInvalidPatch() throws Exception {
        
        Todo item = new Todo("some title", "some description", true);
        item.setId("123456");
        
        HttpResponse response = patch(item);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode()); //Not found
    }
    

    private Todo testPatchDone() throws Exception {

        boolean done = !this.model.getDone();

        Todo todoPatch = new Todo();
        todoPatch.setId(this.model.getId());
        todoPatch.setDone(done);

        return testPatchDone(todoPatch, this.model);
    }

    private Todo testPatchDone(Todo todoPatch, Todo todo) throws Exception {
        
        HttpResponse response = patch(todoPatch);
        
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        
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
        
        //find an item
        Todo found = getById(id);

        //delete it
        Response response = delete(found);
        Assert.assertEquals(204, response.getStatus());
        
        //make sure it doesn't exist anymore
        response = get(TODO_API_URL + "/" + found.getId());
        Assert.assertEquals(404, response.getStatus());
    }

    private void testPostMultiple() {

        for (int i = 1; i <= this.numCreate; i++) {

            post(new Todo("Test Todo #" + i, "Todo Description #" + i));

        }
    }

    private Response markDone(String id) throws Exception {

        String url = TODO_API_URL + "/" + id + "/done";
        Response response = get(url);
        return response;
    }

    private Response markUndone(String id) throws Exception {

        String url = TODO_API_URL + "/" + id + "/undone";
        Response response = get(url);
        return response;

    }

    private Todo getById(String id) throws Exception {

        Response response = get(TODO_API_URL + "/" + id);
        Assert.assertEquals(200, response.getStatus());
        Todo found = response.readEntity(Todo.class);

        return found;
        
    }

    private void verifyItemMatch(Todo expected, Todo actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getDone(), actual.getDone());
    }

}
