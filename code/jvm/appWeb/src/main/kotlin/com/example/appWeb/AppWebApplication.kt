package com.example.appWeb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AppWebApplication {
//	@Bean
//	fun channelServiceBuilder(): ChannelServicesInterface {
//		return ChannelServices()
//	}
}

fun main(args: Array<String>) {
	runApplication<AppWebApplication>(*args)
}
