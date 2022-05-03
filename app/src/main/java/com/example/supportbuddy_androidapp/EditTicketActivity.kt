package com.example.supportbuddy_androidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.supportbuddy_androidapp.data.Ticket
import com.example.supportbuddy_androidapp.data.TicketRepo
import kotlinx.android.synthetic.main.activity_edit_ticket.*

class EditTicketActivity : AppCompatActivity() {
    private lateinit var ticketList: TicketRepo
    var isEditMode : Boolean = false
    var editTicketId : Int = 0
    var editTicketObject: Ticket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val errorMessage = "No application found to handle action!"
        if(intent.extras != null){
            val b = intent.extras!!
            val editId = b.getInt("editTicketId")
            if(editId != null && editId > 0){
                isEditMode = true
                editTicketId = editId
            }
        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_ticket)

        ticketList = TicketRepo.get()

        DeleteTicketButton.visibility = View.GONE
        ActionsBar.visibility = View.GONE

        if(isEditMode){
            val getOneObserver = Observer<Ticket>{ ticket ->
                if(isEditMode) {
                    editTicketObject = ticket
                    //TODO - Add Edit Functionality
                    DeleteTicketButton.visibility = View.VISIBLE
                    ActionsBar.visibility = View.VISIBLE
                }
            }

            //TODO - Observe getTicketById
        }
        GoBackButton.setOnClickListener {
           endEditTicketActivity()
        }

        DeleteTicketButton.setOnClickListener {
            isEditMode = false

            //TODO - Add Delete Functionality
            //ticketList.deleteTicket(editTicketId)

            Toast.makeText(
                this,
                "Ticket was deleted",
                Toast.LENGTH_SHORT
            ).show()

            Handler(Looper.getMainLooper()).postDelayed({
                endEditTicketActivity()
            }, 1500)
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
                if(!isEditMode) {

                    val newTicket = ticketList.addTicket(newSubject, newMessage, newEmail, newFirstName, newLastName, newPhone)
                    Toast.makeText(
                        this,
                        "Ticket was saved",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({

                        endEditTicketActivity()
                    }, 1500)
                } else {
                    //TODO - Add Edit Functionality (With resolved on ticket)
                }
            }
        }
    }

    private fun endEditTicketActivity() {
        finish()
    }
}