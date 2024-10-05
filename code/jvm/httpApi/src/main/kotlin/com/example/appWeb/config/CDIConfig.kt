package com.example.appWeb.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(EnableAutoConfiguration::class)
@ComponentScan(basePackages = ["services"])
class CDIConfig