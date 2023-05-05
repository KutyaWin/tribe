package com.covenant.tribe.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "path")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class PathConfiguration {
    String home;
    String main;
    String tmp;
    String image;
    String avatar;
    String event;
    String user;
}
