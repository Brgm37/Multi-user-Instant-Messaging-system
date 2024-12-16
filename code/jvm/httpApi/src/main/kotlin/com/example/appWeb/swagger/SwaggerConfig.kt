package com.example.appWeb.swagger

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("CHimp API")
                    .version("1.0")
                    .description(
                        "API for CHimp (Chelas Instant Messaging Project) application that allows " +
                            "users to create and manage channels.",
                    ),
            )

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build()
}