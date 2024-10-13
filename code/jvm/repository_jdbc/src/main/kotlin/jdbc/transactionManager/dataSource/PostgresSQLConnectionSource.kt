package jdbc.transactionManager.dataSource

import jakarta.inject.Named

/**
 * Represents the connection source for PostgresSQL
 */
@Named("PostgresSQLConnectionSource")
class PostgresSQLConnectionSource : ConnectionSource {
    override val connectionUrl: String
        get() = System.getenv("DB_URL")
    override val username: String
        get() = System.getenv("DB_USER")
    override val password: String
        get() = System.getenv("DB_PASSWORD")
    override val poolSize: Int
        get() = System.getenv("DB_POOL_SIZE").toIntOrNull() ?: 10
}
