package org.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pipeline.AuthenticatedUserArgumentResolver
import pipeline.AuthenticationInterceptor

@Configuration
@ComponentScan(basePackages = ["services", "jdbc.transactionManager", "com.example.appWeb", "pipeline"])
class PipelineConfigurer(
    val interceptor: AuthenticationInterceptor,
    val resolver: AuthenticatedUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(resolver)
    }
}

@SpringBootApplication
class HttpApiApplication

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}