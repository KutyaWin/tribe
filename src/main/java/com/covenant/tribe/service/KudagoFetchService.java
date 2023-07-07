package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface KudagoFetchService {
    KudagoEventsResponseDto fetchPosts() throws JsonProcessingException;
}
