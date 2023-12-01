package com.example.plannerdemo.util

import com.example.plannerdemo.domain.TimeSlot
import java.io.Serializable

class RouteResponseData<T>: Serializable {
    var data: T? = null
    var message: String? = null


    constructor()

    constructor(data: T, message: String) {
        this.data = data
        this.message = message
    }

}