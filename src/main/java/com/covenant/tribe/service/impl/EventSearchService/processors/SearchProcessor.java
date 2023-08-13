package com.covenant.tribe.service.impl.EventSearchService.processors;

import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchParams;

public interface SearchProcessor {
    Integer getOrder();

    void setNext(SearchProcessor searchProcessor);

    SearchProcessor getNext();

    void process(SearchParams searchParams);
}
