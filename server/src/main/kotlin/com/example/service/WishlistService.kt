package com.example.service

import com.example.dao.dao
import com.example.models.WishlistCourse

class WishlistService {
    suspend fun addToWishlist(userId: String, course: WishlistCourse): WishlistCourse? {
        return dao.addCourseToWishlist(userId, course)
    }

    suspend fun removeFromWishlist(userId: String, courseId: String): Boolean {
        return dao.removeCourseFromWishlist(userId, courseId)
    }

    suspend fun getUserWishlist(userId: String): List<WishlistCourse> {
        return dao.getUserWishlist(userId)
    }
}

val wishlistService: WishlistService = WishlistService()
