package com.covenant.tribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Documentation http://localhost:{EXTERNAL_APP_PORT}/swagger-ui/index.html
@SpringBootApplication
public class TribeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TribeApplication.class, args);
    }

}
