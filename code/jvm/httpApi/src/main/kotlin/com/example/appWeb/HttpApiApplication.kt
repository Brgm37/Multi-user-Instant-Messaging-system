package com.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["transactionManager", "services"])
class HttpApiApplication

fun main(args: Array<String>) {
	runApplication<HttpApiApplication>(*args)
}
