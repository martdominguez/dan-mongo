package com.example.storeordersmongo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI storeOrdersOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Store Orders Mongo API")
                        .description("Educational Spring Boot + MongoDB backend for managing store orders.")
                        .version("v1")
                        .contact(new Contact().name("Codex Learning Project"))
                        .license(new License().name("Educational use")));
    }
}
