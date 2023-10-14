package com.example.dao

import com.example.models.Course
import com.example.models.User
import com.example.models.Users
import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.UserCourses
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id].toString(),
        username = row[Users.username]
    )

    private fun resultRowToCourse(row: ResultRow) = Course(
        id = row[UserCourses.courseId],
        times = row[UserCourses.times]
    )
    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(id: String): User? = dbQuery {
        Users.select { Users.id eq UUID.fromString(id)}
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addNewUser(username: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun deleteUser(id: String): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq UUID.fromString(id) } > 0
    }

    override suspend fun updateUserCourses(userIdArg: String, courses: List<Course>): Boolean = dbQuery {
       try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg)}.count() > 0

                if (userExists) {
                    UserCourses.deleteWhere { UserCourses.userId eq UUID.fromString(userIdArg) }
                    courses.forEach { it ->
                        val courseId = it.id
                        val times = it.times
                        UserCourses.insert {
                            it[UserCourses.userId] = UUID.fromString(userIdArg)
                            it[UserCourses.courseId] = courseId
                            it[UserCourses.times] = times
                        }

                    }
                } else {
                    false
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override suspend fun getAllUserCourses(id: String): List<Course> = dbQuery {
        try {
            transaction {
                val userCoursesQuery = (UserCourses innerJoin Users).slice(UserCourses.courseId, UserCourses.times)
                    .select { Users.id eq UUID.fromString(id) }

                userCoursesQuery.map(::resultRowToCourse)

            }
        } catch (e: Exception) {
            listOf()
        }
    }
}

val dao: DAOFacade = DAOFacadeImpl()