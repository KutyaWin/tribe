package com.covenant.tribe.service.impl;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.facade.EventSearchFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ElasticContainer extends AbstractTestcontainers {
    private static final String ELASTIC_SEARCH_DOCKER = "8.8.2";

    private static final Map<String, String> envsForEl = new HashMap<>();

    static {
        envsForEl.put("ES_JAVA_OPTS", "-Xms512m -Xmx512m");
        envsForEl.put("xpack.security.enabled", "false");
        envsForEl.put("discovery.type", "single-node");
    }

    @Container
    protected static final ElasticsearchContainer elasticContainer;

    static {
        try {
            elasticContainer = getElasticContainer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected static ElasticsearchContainer getElasticContainer() throws InterruptedException {
        ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch").withTag(ELASTIC_SEARCH_DOCKER)).withExposedPorts(9200, 9300).withEnv(envsForEl);
        elasticsearchContainer.start();
        elasticsearchContainer.setWaitStrategy(Wait.forHttp("/")
                .forPort(elasticsearchContainer.getMappedPort(9200))
                .forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)));
        return elasticsearchContainer;
    }

    @DynamicPropertySource
    private static void registerElasticProps(DynamicPropertyRegistry registry) {
        String url = elasticContainer.getHost() + ":" + elasticContainer.getMappedPort(9200);
        registry.add("spring.elasticsearch.rest.uris", () -> url);
        registry.add("elastic.enabled", () -> true);
        registry.add("spring.autoconfigure.exclude", () -> "none");
    }
}
