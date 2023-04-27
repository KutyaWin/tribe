package com.covenant.tribe.configuration;

import com.covenant.tribe.client.vk.VkClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = VkClient.class
)
public class NetworkClientsConfiguration {
}
