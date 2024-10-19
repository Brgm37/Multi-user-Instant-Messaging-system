package com.example.appWeb.swagger

import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Configuration
class OpenApiGenerator {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Bean
    fun customOpenAPI(): OpenAPI {
        val openAPI =
            OpenAPI()
                .info(
                    Info()
                        .title("Your API Title")
                        .version("1.0")
                        .description("Your API Description"),
                )

        // Generate the OpenAPI JSON file
        val openApiJson = Json.pretty(openAPI)
        Files.write(Paths.get("openapi.json"), openApiJson.toByteArray())

        return openAPI
    }

    @Bean
    fun publicApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build()

    @Bean
    fun generateOpenApiJson(): Path {
        val openApi = customOpenAPI()
        val restControllers = applicationContext.getBeansWithAnnotation(RestController::class.java)
        restControllers.forEach { (name, bean) ->
            // Process each RestController to include its API documentation
        }
        val openApiJson = Json.pretty(openApi)
        return Files.write(Paths.get("openapi.json"), openApiJson.toByteArray())
    }
}