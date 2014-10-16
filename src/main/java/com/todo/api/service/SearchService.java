/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.service;

import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import com.todo.api.dao.model.TodoEntity;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import java.util.ArrayList;


//
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * /**
 *
 * @author ruben
 */
@Service
public class SearchService {

    final static Logger logger = LoggerFactory.getLogger(SearchService.class);
    private JestClient jestClient;
    public String url; 
    private String INDEX_TODOS = "todos";
    private String TYPE_TODO = "todo";

    public boolean deleteIndex() {

        verifyClient();

        try {

            // Delete index if it is exists
            DeleteIndex deleteIndex = new DeleteIndex.Builder(INDEX_TODOS).build();

            JestResult result = jestClient.execute(deleteIndex);

            return result.isSucceeded();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;

    }

    public boolean index(TodoEntity item) throws Exception {

        verifyClient();

        try {

            IndicesExists indicesExists = new IndicesExists.Builder(INDEX_TODOS).build();
            JestResult result = jestClient.execute(indicesExists);

            if (!result.isSucceeded()) {
                // Create todos index
                CreateIndex createIndex = new CreateIndex.Builder(INDEX_TODOS).build();
                jestClient.execute(createIndex);
            }

            Index index = new Index.Builder(item).index(INDEX_TODOS)
                    .type(TYPE_TODO).id(item.getId()).build();

            result = jestClient.execute(index);

            logger.debug("index result: " + result.getJsonString());

            return result.isSucceeded();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;

    }

    public boolean index(List<TodoEntity> items) throws Exception {
        verifyClient();

        try {

            IndicesExists indicesExists = new IndicesExists.Builder(INDEX_TODOS).build();
            JestResult result = jestClient.execute(indicesExists);

            if (!result.isSucceeded()) {
                // Create todos index
                CreateIndex createIndex = new CreateIndex.Builder(INDEX_TODOS).build();
                jestClient.execute(createIndex);
            }

            Bulk.Builder builder = new Bulk.Builder();
            for (TodoEntity item : items) {
                builder.addAction(new Index.Builder(item).index(INDEX_TODOS).type(TYPE_TODO).build());
            }
            Bulk bulk = builder.build();

            result = jestClient.execute(bulk);

            return result.isSucceeded();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public TodoEntity findDocument(String id) {

        verifyClient();

        TodoEntity entity = null;

        try {

            Get get = new Get.Builder(INDEX_TODOS, id).type(TYPE_TODO).build();
            JestResult result = jestClient.execute(get);
            entity = result.getSourceAsObject(TodoEntity.class);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return entity;
    }

    public boolean updateDocument(TodoEntity entity) throws Exception {

        verifyClient();


//"script" : "ctx._source.name_of_new_field = \"value_of_new_field\""   
//
//   String script = "{\n" +
//                "    \"script\" : \"ctx._source.tags += tag\",\n" +
//                "    \"params\" : {\n" +
//                "        \"tag\" : \"blue\"\n" +
//                "    }\n" +
//                "}";
//        
        String script = "";

        try {

            Update update = new Update.Builder(script)
                    .index(INDEX_TODOS).type(TYPE_TODO).id(entity.getId()).build();

            JestResult result = jestClient.execute(update);

            return result.isSucceeded();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;

    }

    public boolean deleteDocument(String id) {

        verifyClient();

        try {

            Delete delete = new Delete.Builder(id)
                    .index(INDEX_TODOS).type(TYPE_TODO).build();

            JestResult result = jestClient.execute(delete);

            logger.debug("delete result: " + result);

            return result.isSucceeded();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;

    }

    public List<TodoEntity> searchTodos(String param) {
        
        verifyClient();
        
        try {

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.queryString(param));

            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex(INDEX_TODOS)
                    .addType(TYPE_TODO)
                    .build();

            logger.debug("source builder: " + searchSourceBuilder.toString());

            JestResult result = jestClient.execute(search);

            logger.debug("search result: " + result.getJsonString());
            return result.getSourceAsObjectList(TodoEntity.class);

        } catch (Exception e) {
            logger.error("Search error", e);
        }

        return new ArrayList<TodoEntity>();
    }

    private JestClient jestClient() throws Exception {

        String connectionUrl;

        if (System.getenv("SEARCHBOX_URL") != null) {
            // Heroku
            connectionUrl = System.getenv("SEARCHBOX_URL");

        } else {
            // generic, check your dashboard
            connectionUrl = url;
            
        }

        // Configuration
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(connectionUrl)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }

    private void verifyClient(){
        
        if (this.jestClient == null) {
            try {
                this.jestClient = jestClient();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
    
}
