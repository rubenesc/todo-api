/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.dao;

import com.mongodb.WriteResult;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.ListOptions;
import com.todo.api.domain.ListWrapper;
import com.todo.api.domain.Todo;
import com.todo.api.filters.AppConst;
import java.util.Date;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author ruben
 */
public class TodoMongoDbDaoImpl implements TodoDao {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TodoMongoDbDaoImpl.class);
    
    private MongoOperations mongoOps;
    private static final String COLLECTION = "todo";

    public TodoMongoDbDaoImpl(MongoOperations mongoOps) {
        this.mongoOps = mongoOps;
    }

    public void create(TodoEntity item) {
        item.setCreated(new Date());
        this.mongoOps.insert(item, COLLECTION);
    }
    
    public List<TodoEntity> find(ListOptions opts) {
        
        Query query = buildPaginationQuery(opts);
        query.with(new Sort(Sort.Direction.ASC, "created"));
        
        return mongoOps.find(query, TodoEntity.class, COLLECTION);

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

    public long count() {
        return mongoOps.getCollection(COLLECTION).count();
    }
    
    /**
     * Pagination Query builder
     * 
     * @param opts
     * @return 
     */
    private Query buildPaginationQuery(ListOptions opts) {
        
        Query query = new Query();
        int skip = (opts.getPage() - 1) * opts.getLimit();
        query.skip(skip);
        query.limit(opts.getLimit());
        return query;
    }
}
