package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Value("${server.servlet.context-path}")
    private String basePath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Make Me Top").version("v 0.1"))
                .addServersItem(new Server().url("http://localhost:4401"+basePath));
    }
}
