package com.covenant.tribe.scheduling.notifications;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationsStrategyFactory {

    private Map<NotificationStrategyName, NotificationStrategy> notificationStrategyMap;

    @Autowired
    public NotificationsStrategyFactory(Set<NotificationStrategy> strategies) {
        notificationStrategyMap = strategies.stream().collect(Collectors.toMap(s -> s.getStrategyName(), s -> s));
    }

   public NotificationStrategy find(NotificationStrategyName name) {
       NotificationStrategy notificationStrategy = notificationStrategyMap.get(name);
       Preconditions.checkState(notificationStrategy != null, String.format("Strategy of type %s not found", name.name()));
       return notificationStrategyMap.get(name);
    }
}
