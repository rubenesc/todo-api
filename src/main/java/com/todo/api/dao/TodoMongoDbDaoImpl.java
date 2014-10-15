/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.dao;

import com.mongodb.WriteResult;
import com.todo.api.dao.model.TodoEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author ruben
 */
public class TodoMongoDbDaoImpl implements TodoDao {
    
    
    private MongoOperations mongoOps;
    private static final String COLLECTION = "todo";

    public TodoMongoDbDaoImpl(MongoOperations mongoOps) {
        this.mongoOps = mongoOps;
    }

    public void create(TodoEntity item) {
        item.setCreated(new Date());
        this.mongoOps.insert(item, COLLECTION);
   }

    public void create(List<TodoEntity> items) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<TodoEntity> find() {
        return this.mongoOps.findAll(TodoEntity.class, COLLECTION);
    }

    public TodoEntity find(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoOps.findOne(query, TodoEntity.class, COLLECTION);
    }

    public void update(TodoEntity item) {
        item.setModified(new Date());
        this.mongoOps.save(item, COLLECTION);
    }

    public int delete(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = this.mongoOps.remove(query, TodoEntity.class, COLLECTION);
        return result.getN();
    }

    public int deleteAll() {
        WriteResult result = this.mongoOps.remove(new Query(), COLLECTION);
        return result.getN();
    }
    
    
}
