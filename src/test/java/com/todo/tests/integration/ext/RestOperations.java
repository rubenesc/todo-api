/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests.integration.ext;

import com.google.gson.Gson;
import com.todo.api.domain.Todo;
import com.todo.api.filters.AppConst;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Assert;

/**
 * 
 * Handles Rest Operations (POST, PUT, GET, PATH, DELETE)
 * 
 * @author ruben
 */
public class RestOperations {

    Client client;
    public  String BASE_URL = "http://localhost:8080";
    public  String TODO_API_URL;

    public void init() {

        // ex: http://localhost:8080/todo-api/v1/todo
        TODO_API_URL = BASE_URL + AppConst.TODO_PATH;


        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        client = ClientBuilder.newClient(clientConfig);

    }

    //Basic operations, POST, PUT, PATCH, GET, DELETE
    
    //POST
    public Response post(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL);

        Invocation.Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.post(Entity.entity(item, MediaType.APPLICATION_JSON));
        return response;
    }
    
    
    //GET
    public Response get(){
        
        return get(TODO_API_URL);
        
    }

    public Response get(String url){
    
        WebTarget webTarget = client.target(url);

        Invocation.Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.get();
        return response;
        
    }
    
    //UPDATE
    public Response update(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Invocation.Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.put(Entity.entity(item, MediaType.APPLICATION_JSON));
        return response;
    }
    
    
    /**
     * Partial update with PATCH method. Used Apache httpClient instead of
     * jersey, due to lack of support of PATCH method.
     *
     * @throws Exception
     */
    public HttpResponse patch(Todo item) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPatch httpPatch = new HttpPatch(TODO_API_URL + "/" + item.getId());

        Gson gson = new Gson();
        StringEntity entity = new StringEntity(gson.toJson(item));
        entity.setContentType(MediaType.APPLICATION_JSON);

        httpPatch.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPatch);
        return response;
    }
    
    //Delete
    public  Response delete(Todo item) {

        WebTarget webTarget = client.target(TODO_API_URL + "/" + item.getId());

        Invocation.Builder request = webTarget.request();
        request.header("Content-type", MediaType.APPLICATION_JSON);

        Response response = request.delete();
        
        return response;
    }
    
    
    
    
}
