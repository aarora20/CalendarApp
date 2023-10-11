package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Calendar(val courses: List<Course>)