package com.igorunderplayer.unreminder.models

import com.google.firebase.Timestamp

class Reminder {
    var title: String = ""
    var details: String = ""
    var id: String = ""
    var date: Timestamp? = null
    var createdAt: Timestamp? = null
}