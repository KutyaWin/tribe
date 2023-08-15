package com.covenant.tribe.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.RestClients;

@Configuration
@Slf4j
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private String elasticUris;

    @Value("${spring.elasticsearch.rest.username}")
    private String elasticUsername;

    @Value("${spring.elasticsearch.rest.password}")
    private String elasticPassword;

    @Override
    public ClientConfiguration clientConfiguration() {
        log.info("Connecting to elastic server: " + elasticUris);
        return ClientConfiguration.builder().connectedTo(elasticUris).withBasicAuth(elasticUsername, elasticPassword).build();
    }
}
