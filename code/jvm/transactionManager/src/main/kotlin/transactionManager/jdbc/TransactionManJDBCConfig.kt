package org.example.transactionManager.jdbc

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Named

/**
 * TransactionManagerJDBC class configuration.
 *
 * Provides the database URL, user, and password from the environment.
 *
 * @see TransactionManJDBCConfig.provideDBUrl
 * @see TransactionManJDBCConfig.provideDBUser
 * @see TransactionManJDBCConfig.provideDBPassword
 * @see TransactionManJDBCConfig.providePoolSize
 * @throws IllegalArgumentException If the non-optional environment variables are not set.
 */
@ApplicationScoped
class TransactionManJDBCConfig {

	/**
	 * Provides the database URL from the environment.
	 *
	 * - It uses the environment variable `DB_URL`.
	 */
	@Produces
	@Named("DB_URL")
	fun provideDBUrl(): String =
		System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/daw"

	/**
	 * Provides the database user from the environment.
	 *
	 * - It uses the environment variable `DB_USER`.
	 */
	@Produces
	@Named("DB_USER")
	fun provideDBUser(): String =
		System.getenv("DB_USER") ?: throw IllegalArgumentException("DB_USER not set")

	/**
	 * Provides the database password from the environment.
	 *
	 * - It uses the environment variable `DB_PASSWORD`.
	 */
	@Produces
	@Named("DB_PASSWORD")
	fun provideDBPassword(): String =
		System.getenv("DB_PASSWORD") ?: throw IllegalArgumentException("DB_PASSWORD not set")

	/**
	 * Provides the pool size from the environment.
	 *
	 * - It uses the environment variable `POOL_SIZE` if set.
	 */
	@Produces
	@Named("POOL_SIZE")
	fun providePoolSize(): Int? =
		System.getenv("POOL_SIZE")?.toIntOrNull()
}
