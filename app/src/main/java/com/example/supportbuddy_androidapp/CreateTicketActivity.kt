package com.example.supportbuddy_androidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supportbuddy_androidapp.data.TicketRepo
import com.example.supportbuddy_androidapp.data.dto.TicketDTO_Out
import kotlinx.android.synthetic.main.activity_create_ticket.*

class CreateTicketActivity : AppCompatActivity() {
    private lateinit var ticketRepo: TicketRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        val errorMessage = "No application found to handle action!"
        if(intent.extras != null){
            val b = intent.extras!!

        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_ticket)

        ticketRepo = TicketRepo.get()

        ActionsBar.visibility = View.GONE

        GoBackButton.setOnClickListener {
           endCreateTicketActivity()
        }

        SaveTicketButton.setOnClickListener {
            val newSubject = TicketSubject.text.toString()
            val newMessage = TicketMessage.text.toString()
            val newEmail = TicketEmail.text.toString()
            val newFirstName = TicketFirstName.text.toString()
            val newLastName = TicketLastName.text.toString()
            val newPhone = TicketPhone.text.toString().toInt()


            if(newSubject.isEmpty() ||
                newMessage.isEmpty() ||
                newEmail.isEmpty() ||
                newFirstName.isEmpty() ||
                newLastName.isEmpty() ||
                newPhone == 0
            ){
                Toast.makeText(this,"Fields cannot be empty!",Toast.LENGTH_SHORT).show()
            } else {
                val newTicket = ticketRepo.addTicket(newSubject, newMessage, newEmail, newFirstName, newLastName, newPhone) as TicketDTO_Out
                Toast.makeText(
                    this,
                    "Ticket was added",
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    endCreateTicketActivity() }, 1500)
            }
        }
    }

    private fun endCreateTicketActivity() {
        finish()
    }
}