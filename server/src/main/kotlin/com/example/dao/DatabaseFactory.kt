package com.example.dao
import com.example.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object DatabaseFactory {
    fun init() {
        val dotenv = dotenv()
        val driverClassName = "org.postgresql.Driver"
        val dbUser = dotenv["DB_USER"]
        val dbPass = dotenv["DB_PASS"]
        val dbName = dotenv["DB_NAME"]
        val instanceConnectionName = dotenv["INSTANCE_CONNECTION_NAME"]
        val jdbcURL = "jdbc:postgresql:///${dbName}"
//        val jdbcURL = "jdbc:postgresql://localhost:5432/calendarApp"
        val database = Database.connect(createHikariDataSource(jdbcURL, driverClassName, dbUser,
            dbPass, instanceConnectionName))

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
        driver: String,
        user: String,
        pass: String,
        instance: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        username = user
        password = pass
        addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory");
        addDataSourceProperty("cloudSqlInstance", instance);
        addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}