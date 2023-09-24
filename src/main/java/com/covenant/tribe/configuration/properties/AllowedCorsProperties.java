package com.covenant.tribe.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "allowed")
@Getter
@Setter
public class AllowedCorsProperties {

    List<String> cors;

}
