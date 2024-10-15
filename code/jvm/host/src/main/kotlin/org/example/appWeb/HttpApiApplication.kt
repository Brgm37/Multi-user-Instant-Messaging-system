package org.example.appWeb

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import jdbc.transactionManager.dataSource.ConnectionSource
import jdbc.transactionManager.dataSource.PostgresSQLConnectionSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
class HttpApiApplication {
    @Bean
    @Profile("jdbc")
    fun sc(): ConnectionSource = PostgresSQLConnectionSource()

    @Bean
    @Profile("jdbc")
    fun jdbc(): TransactionManager = TransactionManagerJDBC(sc())
}

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}