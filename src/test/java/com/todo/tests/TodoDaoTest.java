/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.domain.ListOptions;
import com.todo.api.filters.AppConst;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ruben
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration("file:src/main/resources/spring/applicationContext.xml")
public class TodoDaoTest {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TodoDaoTest.class);
    
    @Autowired
    TodoDao todoDao;

    TodoEntity entity; //global test entity
    
    int numCreate; //Numer of documents to create

    @Before
    public void setUp() throws Exception {
        
        String property = System.getProperty("spring.profiles.active");
        logger.info("spring.profiles.active ["+property+"]");
        
        this.deleteAll();
        numCreate = 26;
    }

    @Test
    public void tests() {

        this.create();

        this.find();
        this.invalidFind();

        this.update();

        this.delete();
        this.invalidDelete();

        this.testPagination();

    }

    private void testPagination() {

        this.deleteAll();
        this.createMultipleDocuments(this.numCreate);

        this.paginate(AppConst.PAG_DEFAULT_LIMIT);
        this.paginate(1);
        this.paginate(10);
        this.paginate(20);
        this.paginate(30);
        
    }

    private void deleteAll() {
        int total = todoDao.deleteAll();
        long count = todoDao.count();
        Assert.assertEquals("Clean TODO table", 0, count);
    }

    public void create() {
        this.entity = new TodoEntity("Todo 1", "Description for todo #1");
        todoDao.create(entity);
        String entityId = entity.getId();
        Assert.assertNotNull(entityId);

        //Count Items in DB
        long count = todoDao.count();
        Assert.assertEquals(1, count);
    }

    public void invalidFind() {
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

    private void invalidDelete() {

        int total = todoDao.delete("123456");
        Assert.assertEquals("Delete TODO item", 0, total);

    }

    private void delete() {

        int total = this.todoDao.delete(this.entity.getId());
        Assert.assertEquals("Delete TODO item", 1, total);

        TodoEntity found = todoDao.find(this.entity.getId());
        Assert.assertNull(found);

    }

    private void createMultipleDocuments(int total) {

        for (int i = 1; i <= total; i++) {
            todoDao.create(new TodoEntity("Test Todo #" + i, "Todo Description #" + i));
        }

        long count = this.todoDao.count();
        Assert.assertEquals("Did not create the total amout of documents", total, count);

    }

    private void paginate(int pageLimit) {
        
        int counter = 0;
        pageLimit = Math.abs(pageLimit);
        long totalDocuments = this.todoDao.count();
        int totalPages = (int) Math.ceil((double)totalDocuments / pageLimit);

        for (int page = 1; page <= totalPages; page++) {
            
            List<TodoEntity> items = this.todoDao.find(new ListOptions(page, pageLimit));
            
            for (TodoEntity item : items) {
//                logger.info("item [" + item + "]");
                counter++;
            }
            
        }

        Assert.assertEquals("Did not paginate all items", totalDocuments, counter);
    }

}
