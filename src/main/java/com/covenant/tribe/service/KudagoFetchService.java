package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface KudagoFetchService {

    Map<Long, KudagoEventDto> fetchPosts(Long sincePublicationDate) throws JsonProcessingException;

    List<KudagoEventDto> fetchPosts(Integer page, Integer pageSize) throws JsonProcessingException;
}
