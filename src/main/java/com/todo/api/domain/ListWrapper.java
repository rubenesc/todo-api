/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.domain;

import java.util.List;

/**
 *
 * @author ruben
 */
public class ListWrapper<T> {
    
    private int page;
    private int limit;
    private long total;
    private List<T> items;

    public ListWrapper() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    
    
}
