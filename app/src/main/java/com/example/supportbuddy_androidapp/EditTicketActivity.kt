package com.example.supportbuddy_androidapp

import android.R.attr
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.supportbuddy_androidapp.data.*
import com.example.supportbuddy_androidapp.data.callback.ITicketCallback
import com.example.supportbuddy_androidapp.data.models.Answer
import com.example.supportbuddy_androidapp.data.models.Ticket
import com.example.supportbuddy_androidapp.utils.UIUtils
import kotlinx.android.synthetic.main.activity_edit_ticket.*


class EditTicketActivity : AppCompatActivity() {
    private lateinit var ticketRepo: TicketRepo
    private var editTicketId : Int = 0
    private var editTicketObject : Ticket? = null
    private var answerText = ""
    private var isClosed : Boolean = false

    private var attachmentId : Int = -1

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

        //Handler for Back Button
        GoBackButton.setOnClickListener {
            endEditTicketActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val result: Int = data!!.getIntExtra("attachmentId",-1)

                tvImagePath.hint = "ID: " + result

                attachmentId = result
            }
        }
    }


    private fun onClickPhoto(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onResume(){
        super.onResume()
        setTicketAdapter()
    }

    private fun deleteTicket() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Delete:")
        builder.setMessage("Are you sure you want to delete this ticket?")
        builder.setPositiveButton("Ok") { _, _ ->
            ticketRepo.deleteTicket(editTicketId)
            Handler(Looper.getMainLooper()).postDelayed({
                endEditTicketActivity()
            }, 1500)
            Toast.makeText(
                this,
                "Ticket was deleted",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()

    }

    private fun closeTicket() {
        ticketRepo.closeTicket(editTicketId)
        setTicketAdapter()
    }

    private fun setTicketAdapter(){
        Log.d("XYZ", "It runs :-)")
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
        val adapter = AnswerAdapter(this, answers.toTypedArray())


        if(ticket.status == "Closed"){
            isClosed = true
        }

        if(!isClosed) {

            TicketAnswerButton.isEnabled = true
            TicketCloseButton.isEnabled = true
            TicketDeleteButton.isEnabled = false
            AddAnswerLayout.visibility = LinearLayout.VISIBLE

            TicketAnswerButton.setOnClickListener {
                // Here you get get input text from the Edittext
                answerText = addAnswerText.text.toString()
                ticketRepo.addTicketAnswer(editTicketId, answerText, attachmentId)
                Toast.makeText(this, "Answer was added :-)", Toast.LENGTH_SHORT).show()
                finish();
                startActivity(getIntent());

            }
            addAttachmentButton.setOnClickListener {
                onClickPhoto()
            }
            TicketCloseButton.setOnClickListener{
                closeTicket()
                isClosed = true
            }

        } else {
            TicketAnswerButton.isEnabled = false
            TicketCloseButton.isEnabled = false
            TicketDeleteButton.isEnabled = true
            AddAnswerLayout.visibility = LinearLayout.GONE

            TicketDeleteButton.setOnClickListener{
                deleteTicket()
            }
        }

        lvAnswers.adapter = adapter
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