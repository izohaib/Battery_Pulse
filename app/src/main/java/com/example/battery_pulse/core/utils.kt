package com.example.battery_pulse.core

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}

object Utils {
    fun formatMinutes(minutes: Int): String {
        return if (minutes < 60) {
            "$minutes min"
        } else {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins == 0) "$hours hr" else "$hours hr $mins min"
        }
    }
}