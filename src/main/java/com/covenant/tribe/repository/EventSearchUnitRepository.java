package com.covenant.tribe.repository;


import com.covenant.tribe.domain.event.search.EventSearchUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchUnitRepository extends ElasticsearchRepository<EventSearchUnit, String> {
    
}