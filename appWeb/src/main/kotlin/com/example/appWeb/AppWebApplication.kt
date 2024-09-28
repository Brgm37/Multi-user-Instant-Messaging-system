package com.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppWebApplication

fun main(args: Array<String>) {
	runApplication<AppWebApplication>(*args)
}
