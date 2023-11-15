package com.example.dao

import com.example.models.*

interface DAOFacade {

    // Users/Auth
    // Maybe don't need
    suspend fun allUsers(): List<User>
    suspend fun user(id: String): User?
    suspend fun addNewUser(username: String, password: String): User?

    suspend fun findUser(username: String): User?

    suspend fun findSimilarUsers(username: String): List<User>

    suspend fun deleteUser(id: String): Boolean

    // Friends
    suspend fun addFriend(userId: String, friendId: String): Friend?

    suspend fun findFriends(userId: String): List<User>

    suspend fun findAllRequests(userId: String): List<Friend>

    suspend fun findAllPending(userId: String): List<User>

    suspend fun acceptFriendRequest(userId: String, friendId: String): Friend?

    suspend fun findFriendRequest(userId: String, friendId: String): Boolean

    suspend fun rejectFriendRequest(userId: String, friendId: String): Boolean

    // User Courses
    suspend fun addUserCourse(userIdArg: String, course: UserCourse): UserCourse?
    suspend fun updateUserCourses(userIdArg: String, courses: List<UserCourse>): Boolean
    suspend fun getAllUserCourses(id: String): List<UserCourse>

    // Wishlist
    suspend fun addCourseToWishlist(userIdArg: String, wishlistCourse: WishlistCourse): WishlistCourse?
    suspend fun removeCourseFromWishlist(userIdArg: String, subjectCode: String, catalogNumber: String): Boolean
    suspend fun getUserWishlist(userIdArg: String): List<WishlistCourse>

    // Custom Calendars and UserCalendarCourses (Playground feature)

    suspend fun addUserCalendarCourse(userIdArg: String, calendarIdArg: String, course: UserCalendarCourse): UserCalendarCourse?
    suspend fun updateUserCalendarCourses(userIdArg: String, calendarIdArg: String, courses: List<UserCalendarCourse>): Boolean
    suspend fun getAllUserCalendarCourses(userId: String, calendarId: String): List<UserCalendarCourse>

    suspend fun addCustomCalendar(userIdArg: String, calendar: CustomCalendarParams): CustomCalendar?

    suspend fun deleteCustomCalendar(userIdArg: String, calendarId: String): Boolean

    suspend fun getCustomCalendars(userIdArg: String): List<CustomCalendar>

}


