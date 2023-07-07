package com.covenant.tribe.service.impl;

import com.covenant.tribe.TestcontainersTest;
import com.covenant.tribe.TribeApplication;
import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import com.covenant.tribe.service.KudagoFetchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TribeApplication.class})
@ActiveProfiles("test")
public class KudagoFetchServiceImplIT extends TestcontainersTest {
    @Autowired
    private KudagoFetchService kudaGoFetchService;

    @Test
    public void eventsAreRetrievable() throws JsonProcessingException {
        KudagoEventsResponseDto kudagoEventDtos = kudaGoFetchService.fetchPosts();
        System.out.println(kudagoEventDtos);
    }
}
