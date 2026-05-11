package com.example.battery_pulse.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun previewDate(pattern: String): String =
    try {
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
    } catch (e: Exception) {
        e.stackTrace
        pattern
    }
