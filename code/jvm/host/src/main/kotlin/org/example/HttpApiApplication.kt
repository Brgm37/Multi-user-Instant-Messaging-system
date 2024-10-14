package org.example

import com.example.appWeb.filter.ValidateCookie
import interfaces.UserServicesInterface
import jakarta.inject.Inject
import jakarta.inject.Named
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
        @Named("UserServices") private val services: UserServicesInterface,
    ) {
        @Bean
        fun addFilter(): FilterRegistrationBean<ValidateCookie> {
            val registrationBean = FilterRegistrationBean<ValidateCookie>()
            registrationBean.filter = ValidateCookie(services)
            registrationBean.addUrlPatterns("/verified/*")
            return registrationBean
        }
    }

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}