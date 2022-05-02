package com.example.supportbuddy_androidapp

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.supportbuddy_androidapp.data.ICallback
import com.example.supportbuddy_androidapp.data.Ticket
import com.example.supportbuddy_androidapp.data.TicketRepo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var ticketsList : TicketRepo

    val mRepo = TicketRepo()

    companion object{
        val TAG = "xyz"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        mRepo.getAll(object:ICallback{
            override fun onTicketsReady(tickets: List<Ticket>) {
                setupListView(tickets)
            }
        })
    }

    fun setupListView(tickets: List<Ticket>) {

        val adapter = TicketAdapter(this, tickets.toTypedArray())
        lvTickets.adapter = adapter

        Log.d(TAG, "Listview adapter created with ${tickets.size} tickets")
    }

    internal class TicketAdapter(context: Context, private val tickets: Array<Ticket>) : ArrayAdapter<Ticket>(context, 0, tickets)
    {
        // These colors are used to toggle the variable background of the list items.
        private val colours = intArrayOf(
            Color.parseColor("#AAAAAA"),
            Color.parseColor("#CCCCCC")
        )

        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            var v1: View? = v
            if (v1 == null) {
                val mInflater = LayoutInflater.from(context)
                v1 = mInflater.inflate(R.layout.cell_extended, null)

            }
            val resView: View = v1!!
            resView.setBackgroundColor(colours[position % colours.size])
            val t = tickets[position]
            val nameView = resView.findViewById<TextView>(R.id.tvNameExt)
            val subjectView = resView.findViewById<TextView>(R.id.tvSubjectExt)
            val statusView = resView.findViewById<ImageView>(R.id.imgStatusExt)
            nameView.text = t.firstName + " " + t.lastName
            subjectView.text = t.subject

            statusView.setImageResource(if (t.status == "Closed") R.drawable.ok else R.drawable.notok)

            return resView
        }
    }

}