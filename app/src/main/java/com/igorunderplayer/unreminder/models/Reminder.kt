package com.igorunderplayer.unreminder.models

import com.google.firebase.Timestamp

class Reminder {
    lateinit var title: String
    lateinit var details: String
    lateinit var id: String
    lateinit var date: Timestamp
    lateinit var createdAt: Timestamp
}