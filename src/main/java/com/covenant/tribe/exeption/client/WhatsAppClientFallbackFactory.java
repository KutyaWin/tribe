package com.covenant.tribe.exeption.client;

import com.covenant.tribe.client.whatsapp.WhatsAppClient;
import com.covenant.tribe.client.whatsapp.dto.WhatsAppVerificationMsgDto;
import com.covenant.tribe.exeption.auth.WhatsAppSendingCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WhatsAppClientFallbackFactory implements FallbackFactory<WhatsAppClient> {
    @Override
    public WhatsAppClient create(Throwable throwable) {
        return new WhatsAppClient() {
            @Override
            public ResponseEntity<?> sendVerificationCode(String token, String apiVersion, String phoneId, WhatsAppVerificationMsgDto whatsAppVerificationMsgDto) {
                if (throwable instanceof Exception) {

                    String errorMsg = "Error with verification code sending";
                    log.error(errorMsg);
                    throw new WhatsAppSendingCodeException(errorMsg);
                }
                return ResponseEntity.ok("");
            }
        };
    }
}
