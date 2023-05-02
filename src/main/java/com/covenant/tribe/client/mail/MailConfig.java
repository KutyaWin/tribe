package com.covenant.tribe.client.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private Map<String, String> properties;
}
