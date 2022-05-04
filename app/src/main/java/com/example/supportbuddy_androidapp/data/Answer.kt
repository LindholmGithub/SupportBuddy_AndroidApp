package com.example.supportbuddy_androidapp.data

import java.time.LocalDateTime

data class Answer(
    val id: Int,
    val authorFirstName: String,
    val authorLastName: String,
    val message: String,
    val timeStamp: LocalDateTime
)
