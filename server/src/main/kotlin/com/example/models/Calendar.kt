package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Calendar(val courses: List<UserCourse>)