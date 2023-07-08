package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface KudagoFetchService {

    List<KudagoEventDto> fetchPosts(KudagoClientParams kudagoClientParams) throws JsonProcessingException;

    List<KudagoEventDto> fetchPosts(Integer page, Integer pageSize, KudagoClientParams kudagoClientParams) throws JsonProcessingException;
}
