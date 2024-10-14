package jdbc.transactionManager.dataSource

import jakarta.inject.Named

/**
 * Represents the connection source for PostgresSQL
 */
@Named("PostgresSQLConnectionSource")
class PostgresSQLConnectionSource : ConnectionSource {
    override val connectionUrl: String
        get() = tryCatchGetEnv("DB_URL")
    override val username: String
        get() = tryCatchGetEnv("DB_USER")
    override val password: String
        get() = tryCatchGetEnv("DB_PASSWORD")
    override val poolSize: Int
        get() = tryCatchGetEnv("DB_POOL_SIZE").toIntOrNull() ?: 10

    /**
     * Get the environment variable or throw an exception
     * @param name the name of the environment variable
     */
    private fun tryCatchGetEnv(name: String): String =
        try {
            System.getenv(name) ?: throw IllegalArgumentException("Environment variable $name not found")
        } catch (e: Exception) {
            throw IllegalArgumentException("Environment variable $name not found")
        }
}