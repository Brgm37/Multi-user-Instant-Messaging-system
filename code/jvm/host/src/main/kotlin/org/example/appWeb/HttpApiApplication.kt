package org.example.appWeb

import TransactionManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jdbc.transactionManager.TransactionManagerJDBC
import jdbc.transactionManager.dataSource.ConnectionSource
import jdbc.transactionManager.dataSource.HikariConnectionSource
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
import utils.encryption.AESEncrypt
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt
import javax.sql.DataSource

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
    @Profile("hikari")
    fun cs(): ConnectionSource = HikariConnectionSource()

    @Bean
    @Profile("aes")
    fun aesEncryption(): Encrypt {
        val key = System.getenv("AES_KEY") ?: throw Exception("Missing env var AES_KEY")
        return AESEncrypt(key)
    }

    @Bean
    @Profile("no-encryption")
    fun noEncryption(): Encrypt = DummyEncrypt

    @Bean
    @Profile("hikari")
    fun hikariDc(config: ConnectionSource): DataSource =
        HikariConfig()
            .apply {
                jdbcUrl = config.connectionUrl
                username = config.username
                password = config.password
                maximumPoolSize = config.poolSize
            }.let { HikariDataSource(it) }

    @Bean
    @Profile("jdbc")
    fun jdbc(
        dataSource: DataSource,
        encrypt: Encrypt,
    ): TransactionManager = TransactionManagerJDBC(dataSource, encrypt)
}

fun main(args: Array<String>) {
    runApplication<HttpApiApplication>(*args)
}