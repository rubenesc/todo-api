/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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
public class TodoDaoTest {

    @Autowired
    TodoDao todoDao;

    TodoEntity entity; //global test entity

    @Before
    public void setUp() throws Exception {
        this.deleteAll();
    }

    @Test
    public void daoOperations() {
        
        this.create();
        
        this.find();
        this.invalidFind();
        
        this.update();
        
        this.delete();
        this.invalidDelete();

    }

    private void deleteAll() {
        int total = todoDao.deleteAll();
        List<TodoEntity> items = todoDao.find();
        Assert.assertEquals("Clean TODO table", 0, items.size());
    }

    public void create() {
        this.entity = new TodoEntity("Todo 1", "Description for todo #1");
        todoDao.create(entity);
        String entityId = entity.getId();
        Assert.assertNotNull(entityId);
    }

    public void invalidFind(){
        TodoEntity found = todoDao.find("123456");
        Assert.assertNull(found);
    }
    
    public void find() {
        TodoEntity found = todoDao.find(this.entity.getId());
        Assert.assertNotNull(found);
        Assert.assertEquals(this.entity.getTitle(), found.getTitle());
        Assert.assertEquals(this.entity.getDescription(), found.getDescription());
    }
        
    public void update() {

        this.entity.setTitle(this.entity.getTitle() + " - updated through dao");
        this.entity.setDescription(this.entity.getDescription() + " - updated through dao");
        this.entity.setDone(true);
        todoDao.update(entity);

        TodoEntity found = todoDao.find(this.entity.getId());
        Assert.assertNotNull(found);
        Assert.assertEquals(this.entity.getTitle(), found.getTitle());
        Assert.assertEquals(this.entity.getDescription(), found.getDescription());
        Assert.assertEquals(this.entity.getDone(), found.getDone());
    }

    private void invalidDelete(){

        int total = todoDao.delete("123456");
        Assert.assertEquals("Delete TODO item", 0, total);
        
    }
    
    private void delete() {

        int total = this.todoDao.delete(this.entity.getId());
        Assert.assertEquals("Delete TODO item", 1, total);

        TodoEntity found = todoDao.find(this.entity.getId());
        Assert.assertNull(found);

    }
}
