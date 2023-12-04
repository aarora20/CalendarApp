package com.example.dao
import com.example.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        // with Docker
        val jdbcURL = "jdbc:postgresql://db:5432/calendarApp?user=postgres"
            // for localhost without Docker
//        val jdbcURL = "jdbc:postgresql://localhost:5432/calendarApp"
        val database = Database.connect(createHikariDataSource(jdbcURL, driverClassName))

        transaction(database) {
            // create tables if not already created
            SchemaUtils.create(Users);
            SchemaUtils.create(UserCourses);
            SchemaUtils.create(Wishlists)
            SchemaUtils.create(Friends)
            SchemaUtils.create(CustomCalendars)
            SchemaUtils.create(UserCalendarCourses)
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}