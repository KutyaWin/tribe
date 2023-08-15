package com.covenant.tribe.service.impl.EventSearchService.processors;

import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchParams;
import com.covenant.tribe.service.impl.EventSearchService.processors.SearchProcessor;


public abstract class AbstractSearchProcessor implements SearchProcessor {

    protected Integer order;

    private SearchProcessor next;

    @Override
    public Integer getOrder() {
        return this.order;
    }

    @Override
    public void setNext(SearchProcessor searchProcessor) {
        this.next = searchProcessor;
    }

    @Override
    public SearchProcessor getNext() {
        return this.next;
    }

    protected void processNext(SearchParams searchParams) {
        if (getNext() != null) {
            getNext().process(searchParams);
        }
    }
}
