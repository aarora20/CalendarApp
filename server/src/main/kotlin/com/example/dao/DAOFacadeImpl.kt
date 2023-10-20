package com.example.dao

import com.example.models.User
import com.example.models.Users
import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.UserCourse
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

    private fun resultRowToCourse(row: ResultRow) = UserCourse(
        courseId = row[UserCourses.courseId],
        courseNum = row[UserCourses.courseNum],
        courseTitle = row[UserCourses.courseTitle],
        component = row[UserCourses.component],
        startTime = row[UserCourses.startTime],
        endTime = row[UserCourses.endTime],
        weekPattern = row[UserCourses.weekPattern]
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

    override suspend fun addUserCourse(userIdArg: String, course: UserCourse): UserCourse? = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg) }.count() > 0

                if (userExists) {
                    val courseExists = UserCourses.select { (UserCourses.courseNum eq course.courseNum) and
                            (UserCourses.component eq course.component)}.count() > 0

                    if (!courseExists) {
                        UserCourses.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[courseId] = course.courseId
                            it[courseNum] = course.courseNum
                            it[courseTitle] = course.courseTitle
                            it[component] = course.component
                            it[startTime] = course.startTime
                            it[endTime] = course.endTime
                            it[weekPattern] = course.weekPattern
                        }
                        course
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateUserCourses(userIdArg: String, courses: List<UserCourse>): Boolean = dbQuery {
       try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg)}.count() > 0

                if (userExists) {
                    UserCourses.deleteWhere { userId eq UUID.fromString(userIdArg) }
                    courses.forEach { it ->
                        val courseId = it.courseId
                        val courseNum = it.courseNum
                        val component = it.component
                        val startTime = it.startTime
                        val endTime = it.endTime
                        val weekPattern = it.weekPattern
                        val courseTitle = it.courseTitle
                        UserCourses.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[UserCourses.courseId] = courseId
                            it[UserCourses.courseNum] = courseNum
                            it[UserCourses.courseTitle] = courseTitle
                            it[UserCourses.component] = component
                            it[UserCourses.startTime] = startTime
                            it[UserCourses.endTime] = endTime
                            it[UserCourses.weekPattern] = weekPattern
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

    override suspend fun getAllUserCourses(id: String): List<UserCourse> = dbQuery {
        try {
            transaction {
                val userCoursesQuery = (UserCourses innerJoin Users).slice(UserCourses.courseId, UserCourses.component
                ,UserCourses.courseNum, UserCourses.courseTitle, UserCourses.startTime,
                    UserCourses.endTime, UserCourses.weekPattern)
                    .select { Users.id eq UUID.fromString(id) }

                userCoursesQuery.map(::resultRowToCourse)

            }
        } catch (e: Exception) {
            listOf()
        }
    }
}

val dao: DAOFacade = DAOFacadeImpl()