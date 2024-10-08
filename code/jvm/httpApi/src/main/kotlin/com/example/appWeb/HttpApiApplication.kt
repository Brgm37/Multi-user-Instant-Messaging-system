package com.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["services", "jdbc.transactionManager"])
class HttpApiApplication

fun main(args: Array<String>) {
	runApplication<HttpApiApplication>(*args)
}
