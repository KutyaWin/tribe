package com.covenant.tribe.scheduling.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MessageStrategyFactory {

    private Map<MessageStrategyName, MessageStrategy> notificationStrategyMap;

    @Autowired
    public MessageStrategyFactory(Set<MessageStrategy> strategies) {
        notificationStrategyMap = strategies.stream().collect(Collectors.toMap(s -> s.getStrategyName(), s -> s));
    }

   public MessageStrategy find(MessageStrategyName name) {
        return notificationStrategyMap.get(name);
    }
}
