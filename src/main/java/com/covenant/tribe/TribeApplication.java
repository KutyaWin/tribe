package com.covenant.tribe;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Documentation http://localhost:{EXTERNAL_APP_PORT}/swagger-ui/index.html
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Tribe API",
                version = "v1"
        ),
        servers = {
                @Server(
                    url = "http://localhost:8083/", description = "Local server"
                ),
                @Server(
                        url = "https://dev.tribual.ru/", description = "Dev server"
                ),
                @Server(
                    url = "https://tribual.ru/", description = "Production server"
                )
        }
)
@SecurityScheme(
        name = "BearerJWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Bearer JWT token.")
public class TribeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TribeApplication.class, args);
    }

}
