package com.example.supportbuddy_androidapp.data

interface ICallback {
    fun onTicketsReady(tickets: List<Ticket>)
}