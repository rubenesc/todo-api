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
import org.springframework.test.context.support.AnnotationConfigContextLoader;

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
        this.deleteAll();
        this.create();
        this.find();
        this.update();
        this.delete();

    }

    private void deleteAll() {
        int total = todoDao.deleteAll();
        List<TodoEntity> items = todoDao.find();
        Assert.assertTrue("Clean TODO table", items.size() == 0);
    }

    public void create() {
        this.entity = new TodoEntity("Todo 1", "Description for todo #1");
        todoDao.create(entity);
        String entityId = entity.getId();
        Assert.assertNotNull(entityId);
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

    private void delete() {

        int total = this.todoDao.delete(this.entity.getId());
        Assert.assertTrue("Delete TODO item", total == 1);

        TodoEntity found = todoDao.find(this.entity.getId());
        Assert.assertNull(found);

    }
}
