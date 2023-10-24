package com.covenant.tribe.chat.controller.ws;

import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.service.ChatService;
import com.covenant.tribe.chat.service.UserConnectionService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WsChatController {

    ChatService chatService;
    UserConnectionService userConnectionService;

    @Autowired
    public WsChatController(ChatService chatService, UserConnectionService userConnectionService) {
        this.chatService = chatService;
        this.userConnectionService = userConnectionService;
    }

    public static final String SUBSCRIBE_TO_MESSAGES = "/topic/{subscriber_id}/chat";
    public static final String SUBSCRIBE_TO_USER_CONNECTION = "/topic/{subscribed_id}/connection";


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

    @SubscribeMapping(SUBSCRIBE_TO_USER_CONNECTION)
    public ChatMessageDto subscribeToUserConnection(
            Principal user, @DestinationVariable(value = "user_id") String userId
    ) {
        log.info("User with id {} subscribed to user connection with id {}",
                user.getName(), userId);
        return null;
    }

    @EventListener
    public void handleEvent(SessionConnectEvent connectEvent) {
        Principal user = connectEvent.getUser();
        userConnectionService.userConnected(user);
    }

    @EventListener
    public void handleEvent(SessionDisconnectEvent disconnectEvent) {
        Principal user = disconnectEvent.getUser();
        userConnectionService.userDisconnected(user);
    }

}
