package com.todo.api.dao.model;

import com.todo.api.domain.Todo;
import java.util.Date;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.annotation.Id;

public class TodoEntity {

    @Id
    private String id;
    private String title;
    private String description;
    private Boolean done = false;
    
    private Date created;
    private Date modified;

    public TodoEntity() {
    }

    public TodoEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public TodoEntity(Todo model) throws Exception {
        BeanUtils.copyProperties(this, model);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
    
    @Override
    public String toString() {
        return "TodoEntity{" + "id=" + id + ", title=" + title + ", description=" + description + ", done=" + done + '}';
    }
}
