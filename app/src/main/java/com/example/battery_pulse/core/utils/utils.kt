package com.example.battery_pulse.core.utils

import android.content.Context
import android.content.Intent
import android.provider.MediaStore

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


    fun launchCamera(context: Context) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

}

