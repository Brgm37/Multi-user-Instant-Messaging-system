package services

import jdbc.transactionManager.dataSource.ConnectionSource

object Environment : ConnectionSource {
    override val connectionUrl: String
        get() = "jdbc:postgresql://localhost:5433/daw_test"
    override val username: String
        get() = "postgres"
    override val password: String
        get() = "password"
    override val poolSize: Int
        get() = 10
}