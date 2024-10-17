package controller

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
class TestConfig {
    @Bean
    @Profile("inMem")
    fun trxManagerInMem(): TransactionManager = TransactionManagerInMem()

    @Bean
    @Profile("jdbc_test")
    fun trxManagerJDBC(): TransactionManager =
        TransactionManagerJDBC(
            PGSimpleDataSource()
                .apply {
                    setURL(System.getenv("DB_URL"))
                    user = System.getenv("DB_USER")
                    password = System.getenv("DB_PASSWORD")
                },
        )
}