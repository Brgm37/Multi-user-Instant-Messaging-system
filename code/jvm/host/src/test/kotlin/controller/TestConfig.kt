package controller

import TransactionManager
import jdbc.transactionManager.TransactionManagerJDBC
import mem.TransactionManagerInMem
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import utils.encryption.DummyEncrypt
import utils.encryption.Encrypt

@Component
class TestConfig {
    @Bean
    @Profile("inMem")
    fun trxManagerInMem(): TransactionManager = TransactionManagerInMem()

    @Bean
    @Profile("jdbc_test")
    fun encrypt(): Encrypt = DummyEncrypt

    @Bean
    @Profile("jdbc_test")
    fun trxManagerJDBC(encrypt: Encrypt): TransactionManager =
        TransactionManagerJDBC(
            PGSimpleDataSource()
                .apply {
                    setURL(System.getenv("DB_URL_TEST"))
                },
            encrypt,
        )
}