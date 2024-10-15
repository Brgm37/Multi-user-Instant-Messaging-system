package org.example.appWeb

import com.example.appWeb.filter.ValidateCookie
import interfaces.UserServicesInterface
import jakarta.inject.Inject
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["services", "jdbc.transactionManager", "com.example.appWeb"])
class HttpApiApplication
    @Inject
    constructor(
        private val services: UserServicesInterface,
    ) {
        @Bean
        fun addFilter(): FilterRegistrationBean<ValidateCookie> {
            val registrationBean = FilterRegistrationBean<ValidateCookie>()
            registrationBean.filter = ValidateCookie(services)
            registrationBean.addUrlPatterns("/verified/*")
            // TODO: add verified routes
            return registrationBean
        }
    }

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}