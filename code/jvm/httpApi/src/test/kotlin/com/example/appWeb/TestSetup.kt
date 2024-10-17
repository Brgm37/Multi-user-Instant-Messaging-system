package jdbc

import org.postgresql.ds.PGSimpleDataSource

/**
 * Setup for tests that require a connection to the database.
 */
object TestSetup {
    /**
     * The data source to be used in the tests.
     */
    val dataSource =
        PGSimpleDataSource().apply {
            setURL(getDbUrl())
        }

    private fun getDbUrl() = System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")

    private const val KEY_DB_URL = "DB_URL"
}