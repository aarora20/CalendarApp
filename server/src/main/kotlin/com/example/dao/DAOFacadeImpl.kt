package com.example.dao

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id].toString(),
        username = row[Users.username],
        password = row[Users.password]
    )

    private fun resultRowToFriend(row: ResultRow) = Friend(
        userId = row[Friends.userId].toString(),
        friendId = row[Friends.friendId].toString(),
        status = row[Friends.status]
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

    private fun resultRowToCalendarCourse(row: ResultRow) = UserCalendarCourse(
        courseId = row[UserCalendarCourses.courseId],
        courseNum = row[UserCalendarCourses.courseNum],
        courseTitle = row[UserCalendarCourses.courseTitle],
        component = row[UserCalendarCourses.component],
        startTime = row[UserCalendarCourses.startTime],
        endTime = row[UserCalendarCourses.endTime],
        weekPattern = row[UserCalendarCourses.weekPattern]
    )

    private fun resultRowToCalendar(row: ResultRow) = CustomCalendar(
        id = row[CustomCalendars.id].toString(),
        name = row[CustomCalendars.name]
    )

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(id: String): User? = dbQuery {
        Users.select { Users.id eq UUID.fromString(id)}
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findUser(username: String): User? = dbQuery {
        Users.select { Users.username eq username}
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findSimilarUsers(username: String): List<User> = dbQuery {
        transaction {
            val result = mutableListOf<User>()
            TransactionManager.current().exec("select * from users where SIMILARITY(username, '$username') > 0.4;") { rs ->
                while (rs.next()) {
                    result.add(User(rs.getString("id"), rs.getString("username"),
                        rs.getString("password")))
                }
            }

            result.toList()
        }
    }

    override suspend fun addNewUser(username: String, password: String): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.username] = username
            it[Users.password] = BCrypt.hashpw(password, BCrypt.gensalt()) // to encrypt
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun deleteUser(id: String): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq UUID.fromString(id) } > 0
    }

    override suspend fun addFriend(userId: String, friendId: String): Friend? = dbQuery {
        val insertStatement = Friends.insert {
            it[Friends.userId] = UUID.fromString(userId)
            it[Friends.friendId] = UUID.fromString(friendId)
            it[status] = "pending"
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFriend)
    }

    // Find all friends of a user (accepted requests)
    override suspend fun findFriends(userId: String): List<User> = dbQuery {
        try {
            transaction {
                val friendIds = Friends.slice(Friends.friendId).select {
                    (Friends.userId eq UUID.fromString(userId)) and (Friends.status eq "accepted")
                }.map { it[Friends.friendId] }

                val friends =  Users.select {Users.id inList friendIds }

                friends.map(::resultRowToUser)

            }
        } catch (e: Exception) {
            listOf()
        }
    }


    // Find all pending and accepted friend requests
    override suspend fun findAllRequests(userId: String): List<Friend> = dbQuery {
        Friends.select { Friends.userId eq UUID.fromString(userId)}
            .map(::resultRowToFriend)
    }

    // find all incoming pending requests
    override suspend fun findAllPending(userId: String): List<User> = dbQuery {
        try {
            transaction {
                val friendIds =  Friends.select { (Friends.friendId eq UUID.fromString(userId)) and
                        (Friends.status eq "pending")}
                    .map { it[Friends.userId] }

                val pending =  Users.select {Users.id inList friendIds }

                pending.map(::resultRowToUser)

            }
        } catch (e: Exception) {
            listOf()
        }
    }

    // Find all sent pending requests
    override suspend fun findAllSent(userId: String): List<User> = dbQuery {
        try {
            transaction {
                val friendIds =  Friends.select { (Friends.userId eq UUID.fromString(userId)) and
                        (Friends.status eq "pending")}
                    .map { it[Friends.friendId] }

                val pending =  Users.select {Users.id inList friendIds }

                pending.map(::resultRowToUser)

            }
        } catch (e: Exception) {
            listOf()
        }
    }

    override suspend fun acceptFriendRequest(userId: String, friendId: String): Friend? = dbQuery {
        val updateStatement = Friends.update({ (Friends.userId eq UUID.fromString(friendId)
                and (Friends.friendId eq UUID.fromString(userId))) }) {
            it[status] = "accepted"
        }

        if (updateStatement > 0) {
            val addInverse = Friends.insert {
                it[Friends.userId] = UUID.fromString(userId)
                it[Friends.friendId] = UUID.fromString(friendId)
                it[status] = "accepted"
            }
            addInverse.resultedValues?.singleOrNull()?.let(::resultRowToFriend)
        } else {
            null
        }
    }

    override suspend fun findFriendRequest(userId: String, friendId: String): Boolean = dbQuery {
        val friendRequestExists = Friends.select { (Friends.userId eq UUID.fromString(userId)
                and (Friends.friendId eq UUID.fromString(friendId))) }.count() > 0
        friendRequestExists
    }

    override suspend fun deleteFriendRequest(userId: String, friendId: String): Boolean = dbQuery {
        val deleted = Friends.deleteWhere {  (Friends.userId eq UUID.fromString(friendId)
                and (Friends.friendId eq UUID.fromString(userId)) and (status eq "pending")) }
        if (deleted > 0) {
            true
        } else {
            false
        }
    }

    override suspend fun unfriend(userId: String, friendId: String): Boolean = dbQuery {
        try {
            transaction {
                val deleted = Friends.deleteWhere {  (Friends.userId eq UUID.fromString(friendId)
                        and (Friends.friendId eq UUID.fromString(userId)) and (Friends.status eq "accepted")) }
                val deletedReverse = Friends.deleteWhere {  (Friends.userId eq UUID.fromString(userId)
                        and (Friends.friendId eq UUID.fromString(friendId)) and (Friends.status eq "accepted")) }

                deleted > 0 && deletedReverse > 0
            }
        } catch (e: Exception) {
            false
        }

    }

    override suspend fun addUserCourse(userIdArg: String, course: UserCourse): UserCourse? = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg) }.count() > 0

                if (userExists) {
                    val courseExists = UserCourses.select { (UserCourses.courseNum eq course.courseNum) and
                            (UserCourses.component eq course.component) and
                            (UserCourses.userId eq UUID.fromString(userIdArg))}.count() > 0

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
                        }.resultedValues?.singleOrNull()?.let(::resultRowToCourse)
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

    override suspend fun addCourseToWishlist(userIdArg: String, wishlistCourse: WishlistCourse): WishlistCourse? = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg) }.count() > 0

                if (userExists) {
                    val wishlistCourseExists = Wishlists.select {
                        (Wishlists.subjectCode eq wishlistCourse.subjectCode) and
                        (Wishlists.catalogNumber eq wishlistCourse.catalogNumber) and
                        (Wishlists.termYear eq wishlistCourse.termYear) and
                        (Wishlists.userId eq UUID.fromString(userIdArg))
                    }.count() > 0

                    if (!wishlistCourseExists) {
                        Wishlists.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[subjectCode] = wishlistCourse.subjectCode
                            it[catalogNumber] = wishlistCourse.catalogNumber
                            it[courseTitle] = wishlistCourse.courseTitle
                            it[termYear] = wishlistCourse.termYear
                        }
                        wishlistCourse
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
    override suspend fun removeCourseFromWishlist(userIdArg: String, subjectCode: String, catalogNumber: String, termYear: String): Boolean = dbQuery {
        try {
            transaction {
                val deletedRowCount = Wishlists.deleteWhere {
                    (Wishlists.userId eq UUID.fromString(userIdArg)) and
                            (Wishlists.subjectCode eq subjectCode) and
                            (Wishlists.catalogNumber eq catalogNumber) and
                            (Wishlists.termYear eq termYear)
                }
                deletedRowCount > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    override suspend fun getUserWishlist(userIdArg: String): List<WishlistCourse> = dbQuery {
        try {
            transaction {
                Wishlists.select { Wishlists.userId eq UUID.fromString(userIdArg) }
                    .map {
                        WishlistCourse(
                            subjectCode = it[Wishlists.subjectCode],
                            catalogNumber = it[Wishlists.catalogNumber],
                            courseTitle = it[Wishlists.courseTitle],
                            termYear = it[Wishlists.termYear]
                        )
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    override suspend fun addUserCalendarCourse(userIdArg: String, calendarIdArg: String, course: UserCalendarCourse):
            UserCalendarCourse? = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg) }.count() > 0

                val calendarExists = CustomCalendars.select { CustomCalendars.id eq UUID.fromString(calendarIdArg) }
                    .count() > 0

                if (userExists && calendarExists) {
                    val courseExists = UserCalendarCourses.select { (UserCalendarCourses.courseNum eq course.courseNum) and
                            (UserCalendarCourses.component eq course.component) and
                            (UserCalendarCourses.userId eq UUID.fromString(userIdArg)) and
                            (UserCalendarCourses.calendarId eq UUID.fromString(calendarIdArg))}.count() > 0

                    if (!courseExists) {
                        UserCalendarCourses.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[calendarId] = UUID.fromString(calendarIdArg)
                            it[courseId] = course.courseId
                            it[courseNum] = course.courseNum
                            it[courseTitle] = course.courseTitle
                            it[component] = course.component
                            it[startTime] = course.startTime
                            it[endTime] = course.endTime
                            it[weekPattern] = course.weekPattern
                        }.resultedValues?.singleOrNull()?.let(::resultRowToCalendarCourse)
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

    override suspend fun updateUserCalendarCourses(userIdArg: String, calendarIdArg: String, courses: List<UserCalendarCourse>):
            Boolean = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg)}.count() > 0

                if (userExists) {
                    UserCalendarCourses.deleteWhere { (userId eq UUID.fromString(userIdArg)) and
                            (calendarId eq UUID.fromString(calendarIdArg)) }
                    courses.forEach { it ->
                        val courseId = it.courseId
                        val courseNum = it.courseNum
                        val component = it.component
                        val startTime = it.startTime
                        val endTime = it.endTime
                        val weekPattern = it.weekPattern
                        val courseTitle = it.courseTitle
                        UserCalendarCourses.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[calendarId] = UUID.fromString(calendarIdArg)
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

    override suspend fun getAllUserCalendarCourses(userId: String, calendarId: String):
            List<UserCalendarCourse> = dbQuery {
        try {
            transaction {
                val userCalendarCoursesQuery = (Users innerJoin UserCalendarCourses)
                    .innerJoin(CustomCalendars, { UserCalendarCourses.calendarId}, {CustomCalendars.id})
                    .slice(UserCalendarCourses.courseId, UserCalendarCourses.component
                        ,UserCalendarCourses.courseNum, UserCalendarCourses.courseTitle, UserCalendarCourses.startTime,
                        UserCalendarCourses.endTime, UserCalendarCourses.weekPattern)
                    .select { (CustomCalendars.id eq UUID.fromString(calendarId)) and
                            (Users.id eq UUID.fromString(userId))}

                userCalendarCoursesQuery.map(::resultRowToCalendarCourse)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    override suspend fun addCustomCalendar(userIdArg: String, calendar: CustomCalendarParams): CustomCalendar? = dbQuery {
        try {
            transaction {
                val userExists = Users.select { Users.id eq UUID.fromString(userIdArg) }.count() > 0

                if (userExists) {
                    val calendarExists = CustomCalendars.select {
                        (CustomCalendars.userId eq UUID.fromString(userIdArg) and (
                                CustomCalendars.name eq calendar.name
                                ))
                    }.count() > 0

                    if (!calendarExists) {
                        CustomCalendars.insert {
                            it[userId] = UUID.fromString(userIdArg)
                            it[name] = calendar.name
                        }.resultedValues?.singleOrNull()?.let(::resultRowToCalendar)
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

    override suspend fun deleteCustomCalendar(userIdArg: String, calendarId: String): Boolean = dbQuery {
        CustomCalendars.deleteWhere { (userId eq UUID.fromString(userIdArg)) and
                (id eq UUID.fromString(calendarId))} > 0
    }

    override suspend fun getCustomCalendars(userIdArg: String): List<CustomCalendar> = dbQuery {
        CustomCalendars.select { CustomCalendars.userId eq UUID.fromString(userIdArg)}.map (::resultRowToCalendar)
    }
}

val dao: DAOFacade = DAOFacadeImpl()