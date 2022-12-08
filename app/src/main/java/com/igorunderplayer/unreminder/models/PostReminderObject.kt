package com.igorunderplayer.unreminder.models

data class PostReminderObject(
    val userId: String,
    val title: String,
    val sendAfter: String,
    val contents: Map<String, String>
)