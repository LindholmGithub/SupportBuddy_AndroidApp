package com.example.supportbuddy_androidapp.data.callback

import com.example.supportbuddy_androidapp.data.models.Ticket

interface ITicketCallback {
    fun onTicketReady(ticket: Ticket)
}