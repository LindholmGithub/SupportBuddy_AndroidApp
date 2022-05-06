package com.example.supportbuddy_androidapp.data

data class Ticket(
    val id: Int,
    val status: String,
    val subject: String,
    val message: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: Int,
    val answers: Array<Answer>,
    val timeStamp: String

)
