package org.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["services", "jdbc.transactionManager", "com.example.appWeb"])
class HttpApiApplication

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}