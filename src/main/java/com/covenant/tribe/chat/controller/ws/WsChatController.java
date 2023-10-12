package com.covenant.tribe.chat.controller.ws;

import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.service.ChatService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WsChatController {

    ChatService chatService;

    @Autowired
    public WsChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    public static final String SUBSCRIBE_TO_MESSAGES = "/topic/{user_id}/chat";

    @MessageMapping("/chat/{chat_id}")
    public String processMessage(
            @Payload String chatMessage, Principal principal,
            @DestinationVariable(value = "chat_id") String chatId
    ) {
        chatService.sendMessageToSubscribers(
                Long.valueOf(principal.getName()),
                Long.valueOf(chatId),
                chatMessage
        );
        return "";
    }

    @SubscribeMapping(SUBSCRIBE_TO_MESSAGES)
    public ChatMessageDto subscribeToChats() {
        return null;
    }


}
