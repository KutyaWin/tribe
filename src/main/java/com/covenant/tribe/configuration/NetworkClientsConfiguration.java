package com.covenant.tribe.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        basePackages = "com.covenant.tribe.client"
)
public class NetworkClientsConfiguration {
}
