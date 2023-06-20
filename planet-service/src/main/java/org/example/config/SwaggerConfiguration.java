package org.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
        final String secSchemeName = "token";
        return new OpenAPI().info(new Info().title("Make Me Top").version("v 0.1"))
                .addServersItem(new Server().url("http://10.254.7.231:4401" + basePath));
    }
}
