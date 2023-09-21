package com.covenant.tribe.client.kudago;

import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "kudago-client")
public interface KudagoClient {

    @GetMapping("/events/")
    ResponseEntity<KudagoEventsResponseDto> getEvents(@SpringQueryMap EventRequestQueryMap q);
}
