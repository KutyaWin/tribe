package com.covenant.tribe.service.impl;

import com.covenant.tribe.TestcontainersTest;
import com.covenant.tribe.TribeApplication;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.service.KudagoFetchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TribeApplication.class})
@ActiveProfiles("test")
public class KudagoFetchServiceImplIT extends TestcontainersTest {
    @Autowired
    private KudagoFetchService kudaGoFetchService;

    @Test
    public void eventsAreRetrievable() throws JsonProcessingException {
        OffsetDateTime now = OffsetDateTime.now().minusDays(2);
        Map<Long, KudagoEventDto> kudagoEventDtos = kudaGoFetchService.fetchPosts(30L);
        System.out.println(kudagoEventDtos);
    }
}
