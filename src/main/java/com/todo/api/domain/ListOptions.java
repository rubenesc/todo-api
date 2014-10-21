/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.domain;

import java.util.Set;

/**
 *
 * @author ruben
 */
public class ListOptions {
    
    private Integer page;
    private Integer limit;
    private Set criteria;

    public ListOptions() {
    }

    public ListOptions(Integer page) {
        this.page = page;
    }
    
    public ListOptions(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }
    
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Set getCriteria() {
        return criteria;
    }

    public void setCriteria(Set criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "ListOptions{" + "page=" + page + ", limit=" + limit + ", criteria=" + criteria + '}';
    }
    
}
