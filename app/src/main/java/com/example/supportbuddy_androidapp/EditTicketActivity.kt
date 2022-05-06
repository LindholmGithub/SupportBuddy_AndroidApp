package com.example.supportbuddy_androidapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.TextView
import com.example.supportbuddy_androidapp.data.*
import com.example.supportbuddy_androidapp.utils.UIUtils
import kotlinx.android.synthetic.main.activity_edit_ticket.*
import kotlinx.android.synthetic.main.activity_edit_ticket.GoBackButton

class EditTicketActivity : AppCompatActivity() {
    private lateinit var ticketRepo: TicketRepo
    private var editTicketId : Int = 0
    private var editTicketObject : Ticket? = null

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
        setContentView(R.layout.activity_edit_ticket)

        supportActionBar?.hide()
        TicketRepo.initialize(this)

        setTicketAdapter()

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
        val answers: List<Answer> = ticket.answers.toList()
        lvAnswers.adapter = AnswerAdapter(this, answers.toTypedArray())
        UIUtils().setListViewHeightBasedOnItems(lvAnswers)
    }

    private fun endEditTicketActivity() {
        finish()
    }

    internal class AnswerAdapter(context: Context, private val answers: Array<Answer>) : ArrayAdapter<Answer>(context, 0, answers) {
        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            var v1: View? = v
            if (v1 == null) {
                val mInflater = LayoutInflater.from(context)
                v1 = mInflater.inflate(R.layout.answer_cell, null)

            }
            val resView: View = v1!!
            val a = answers[position]
            val nameView = resView.findViewById<TextView>(R.id.tvAnswerName)
            val messageView = resView.findViewById<TextView>(R.id.tvAnswerMessage)
            val dateView = resView.findViewById<TextView>(R.id.tvAnswerDate)
            nameView.text = a.authorFirstName + " " + a.authorLastName
            messageView.text = a.message
            dateView.text = a.timeStamp


            return resView
        }
    }
}