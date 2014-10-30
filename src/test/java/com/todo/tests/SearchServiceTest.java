/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.tests;

import com.todo.api.dao.TodoDao;
import com.todo.api.dao.model.TodoEntity;
import com.todo.api.service.SearchService;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
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
public class SearchServiceTest {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(SearchServiceTest.class);
    
    @Autowired
    TodoDao todoDao;
    @Autowired
    SearchService searchService;

    @Before
    public void setUp() throws Exception {

        if (searchService.isEnabled()) {
            this.cleanTodoDB();

            boolean result = searchService.deleteIndex();
            Assert.assertTrue("Did not delete index", result);

            result = searchService.createIndex();
            Assert.assertTrue("Did not create index", result);
            
        }

    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void tests() throws Exception {

        if (searchService.isEnabled()) {
            testCrudOperatons();
            testIndexMultipleItems();
        }

    }

    private void testIndexMultipleItems() throws Exception {

        List<TodoEntity> items = buildMockData();

        boolean result;
        for (TodoEntity item : items) {
            this.create(item);
            result = searchService.index(item);
            Assert.assertTrue("faild to index document with id [" + item.getId() + "]", result);
        }
        
        //there are 2 Cat Todo's in the Mock Data
        String query = "cat";
        List<TodoEntity> list = searchService.searchTodos(query);
        Assert.assertNotNull(list);
        Assert.assertEquals("Should match search results", 2, list.size());

    }

    private void testCrudOperatons() throws Exception {

        TodoEntity entity = new TodoEntity("clean the carpet", "vacuum the carpet");
        this.create(entity);

        //create document
        boolean result = searchService.index(entity);
        Assert.assertTrue("faild to index document with id [" + entity.getId() + "]", result);

        //find document and should match
        TodoEntity found = searchService.findDocument(entity.getId());

        Assert.assertNotNull(found);
        Assert.assertEquals(entity.getId(), found.getId());
        Assert.assertEquals(entity.getTitle(), found.getTitle());
        Assert.assertEquals(entity.getDescription(), found.getDescription());
        Assert.assertEquals(entity.getDone(), found.getDone());


        //should update document
//        entity.setDone(true);
//        entity.setDescription("description updated");
//        boolean result = searchService.updateDocument(found);

        //delete document
        boolean deleteResult = searchService.deleteDocument(entity.getId());
        Assert.assertTrue("faild to delete document with id [" + entity.getId() + "]", deleteResult);

        //should not find document
        found = searchService.findDocument(entity.getId());
        Assert.assertNull(found);

    }

    public void create(TodoEntity entity) {
        todoDao.create(entity);
        String entityId = entity.getId();
        Assert.assertNotNull(entityId);
    }

    private void cleanTodoDB() {
        todoDao.deleteAll();
        long count = todoDao.count();
        Assert.assertEquals("Clean TODO table", 0, count);
    }

    private List<TodoEntity> buildMockData() {

        List<TodoEntity> items = new ArrayList<TodoEntity>();

        items.add(new TodoEntity("walk the dog", "take fido to the park"));
        items.add(new TodoEntity("feed the cat", "give him wet food at 8:00pm"));
        items.add(new TodoEntity("buy milk", "2% low fat milk"));
        items.add(new TodoEntity("pay the credit card", "It has to be paid before the 25th of each month"));
        items.add(new TodoEntity("wash the dishes", "don't forget to wash the dishes before goig to bed"));
        items.add(new TodoEntity("clean desk", "organize all the papers and books on your desk"));
        items.add(new TodoEntity("dinner reservation", "make reservation at flower + water"));
        items.add(new TodoEntity("pay Juan back", "pay Juan the money he lent you"));
        items.add(new TodoEntity("call sister", "say hello and see how she's doing"));
        items.add(new TodoEntity("buy flowers", "buy flowers for wife"));
        items.add(new TodoEntity("change light bulb", "in the garage"));
        items.add(new TodoEntity("plan vacations", "plan next trip"));
        items.add(new TodoEntity("wash car", ""));
        items.add(new TodoEntity("laundry", ""));
        items.add(new TodoEntity("buy charger", "buy new phone carger"));
        items.add(new TodoEntity("water plants", "water the garden plants"));
        items.add(new TodoEntity("take cat to vet", "schedule an appointment with the vet"));
        items.add(new TodoEntity("buy dog food", "to go petco and buy dog good"));

        //gibberish todos
        items.add(new TodoEntity("sed magna at", "et magnis dis parturient"));
        items.add(new TodoEntity("dui proin", "pede malesuada in"));
        items.add(new TodoEntity("hac habitasse platea", "eget eleifend luctus"));
        items.add(new TodoEntity("augue luctus tincidunt", "posuere cubilia curae mauris viverra"));
        items.add(new TodoEntity("praesent blandit nam", "viverra pede ac diam cras pellentesque"));
        items.add(new TodoEntity("quam nec dui", "sit amet nunc viverra dapibus nulla"));
        items.add(new TodoEntity("pede malesuada in", "iaculis diam erat fermentum"));
        items.add(new TodoEntity("eu magna vulputate", "fringilla rhoncus mauris enim leo"));
        items.add(new TodoEntity("venenatis lacinia aenean", "rutrum nulla nunc"));
        items.add(new TodoEntity("nullam sit amet", "eleifend pede libero quis"));
        items.add(new TodoEntity("faucibus orci luctus", "ut at dolor quis odio consequat"));
        items.add(new TodoEntity("potenti nullam porttitor", "massa id nisl venenatis lacinia"));
        items.add(new TodoEntity("elit proin interdum", "vulputate ut ultrices vel augue"));
        items.add(new TodoEntity("eu mi", "nisi nam ultrices libero"));
        items.add(new TodoEntity("pellentesque at", "aenean auctor gravida sem praesent id"));
        items.add(new TodoEntity("nunc nisl duis", "risus praesent lectus vestibulum quam"));
        items.add(new TodoEntity("eros vestibulum", "nisi volutpat eleifend donec ut"));
        items.add(new TodoEntity("diam nam tristique", "eleifend luctus ultricies eu nibh quisque"));
        items.add(new TodoEntity("erat nulla tempus", "mus vivamus vestibulum sagittis sapien"));

        return items;
    }
}
