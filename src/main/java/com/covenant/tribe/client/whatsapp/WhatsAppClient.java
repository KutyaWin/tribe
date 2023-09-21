package com.covenant.tribe.client.whatsapp;

import com.covenant.tribe.client.whatsapp.dto.WhatsAppVerificationMsgDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "whatsapp-client")
public interface WhatsAppClient {

    @RequestMapping(method = RequestMethod.POST, value = "/{api-version}/{phone-id}/messages")
    ResponseEntity<?> sendVerificationCode(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable("api-version") String apiVersion,
            @PathVariable("phone-id") String phoneId,
            @RequestBody WhatsAppVerificationMsgDto whatsAppVerificationMsgDto
    );

}
