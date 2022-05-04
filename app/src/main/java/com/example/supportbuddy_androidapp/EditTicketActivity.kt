package com.example.supportbuddy_androidapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.supportbuddy_androidapp.data.ITicketCallback
import com.example.supportbuddy_androidapp.data.Ticket
import com.example.supportbuddy_androidapp.data.TicketRepo
import kotlinx.android.synthetic.main.activity_edit_ticket.*
import kotlinx.android.synthetic.main.activity_edit_ticket.GoBackButton

class EditTicketActivity : AppCompatActivity() {
    private lateinit var ticketRepo: TicketRepo
    private var editTicketId : Int = 0
    private var editTicketObject : Ticket? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val errorMessage = "No application found to handle action!"
        if(intent.extras != null){
            val b = intent.extras!!
            val editId = b.getInt("editTicketId")
            if(editId != null && editId > 0){
                editTicketId = editId
            }
        }
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_ticket)

        TicketRepo.initialize(this)

        setTicketAdapter()

        //editTicketObject = ticketList.getTicketById(editTicketId)
        // val ticket = editTicketObject as Ticket

        //Handler for Back Button
        GoBackButton.setOnClickListener {
            endEditTicketActivity()
        }

    }

    private fun setTicketAdapter(){
        ticketRepo = TicketRepo.get()
        ticketRepo.getTicketById(editTicketId, object: ITicketCallback {
            override fun onTicketReady(ticket: Ticket) {
                setupTicketView(ticket)
            }
        })
    }

    private fun setupTicketView(ticket: Ticket) {
        TicketHeader.setText("Ticket #${ticket.id}")
        TicketFullName.setText("${ticket.firstName} ${ticket.lastName}")
        TicketEmail.setText("E-mail: ${ticket.email}")
        TicketPhone.setText("Phone: ${ticket.phoneNumber}")
        TicketStatus.setText("Ticket Status: ${ticket.status}")
        TicketSubject.setText(ticket.subject)
        TicketMessage.setText(ticket.message)
    }


    private fun endEditTicketActivity() {
        finish()
    }
}