package com.example.myapplication.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun formattedTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
