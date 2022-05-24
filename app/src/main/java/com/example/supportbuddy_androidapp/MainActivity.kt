package com.example.supportbuddy_androidapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.supportbuddy_androidapp.data.AttachmentRepo
import com.example.supportbuddy_androidapp.data.callback.ITicketsCallback
import com.example.supportbuddy_androidapp.data.models.Ticket
import com.example.supportbuddy_androidapp.data.TicketRepo
import com.example.supportbuddy_androidapp.data.models.AuthUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var ticketRepo : TicketRepo
    private var currentAuthUser : AuthUser = AuthUser(null,null)


    companion object{
        val TAG = "xyz"
    }

    /**
     * Method that is ran when the activity runs.
     * @param savedInstanceState Bundle received by the method that runs the activity. Containing information.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        if(intent.extras != null){
            val b = intent.extras!!
            val authUserStatus = b.getString("authUserStatus")
            val basicAuthString = b.getString("basicAuthString")
            if(authUserStatus == "OK" && basicAuthString != null){
                currentAuthUser.status = authUserStatus
                currentAuthUser.basicAuthString = basicAuthString
            }
        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        TicketRepo.initialize(this)
        AttachmentRepo.initialize(this)

        imgLogo.setImageResource(R.drawable.onlylogo)
        AddTicketButton.setOnClickListener {
            val newBundle = Bundle()
            startCreateTicketActivity(newBundle)
        }
        lvTickets.setOnItemClickListener{
            _,_,pos, _ -> onTicketClick(pos)
        }
    }

    /**
     * Method that is ran when the activity runs or is resumed.
     */
    override fun onResume() {
        super.onResume()
        setListTicketsAdapter()
    }

    /**
     * Method that sets up the listView by filling it with all tickets from ticketRepo.
     */
    private fun setListTicketsAdapter(){
        ticketRepo = TicketRepo.get()
        ticketRepo.getAll(object: ITicketsCallback {
            override fun onTicketsReady(tickets: List<Ticket>) {
                setupListView(tickets)
            }
        })
    }

    /**
     * Method that runs the CreateTicketActivity when the user clicks on the
     * Create Ticket button in the application.
     *
     * @param b Bundle setup. Allows parsing data to other activities.
     */
    private fun startCreateTicketActivity(b: Bundle) {
        val newIntent = Intent(this, CreateTicketActivity::class.java)
        newIntent.putExtras(b)
        startActivity(newIntent)
    }

    /**
     * Method that runs the startEditTicketActivity method when the user clicks on a specific Ticket in the ListView.
     *
     * @param position Position of the ticket that was clicked.
     */
    private fun onTicketClick(position: Int){
        val clickedTicket = lvTickets.getItemAtPosition(position) as Ticket
        val newBundle = Bundle()
        newBundle.putInt("editTicketId", clickedTicket.id)
        newBundle.putString("editTicketStatus", clickedTicket.status)
        startEditTicketActivity(newBundle)
    }

    /**
     * Method that starts the EditTicketActivity. Called from the onTicketClick method.
     * @param b Bundle containing information about the clicked ticket from the onTicketClick method.
     */
    private fun startEditTicketActivity(b: Bundle) {
        val newIntent = Intent(this, EditTicketActivity::class.java)
        newIntent.putExtras(b)
        startActivity(newIntent)
    }

    /**
     * Method that sets the adapter of the listView to be the TicketAdapter.
     * @param tickets List of tickets received from setListTicketsAdapter()
     */
    fun setupListView(tickets: List<Ticket>) {
        ticketRepo = TicketRepo.get()
        val adapter = TicketAdapter(this, tickets.toTypedArray())
        lvTickets.adapter = adapter

        Log.d(TAG, "Listview adapter created with ${tickets.size} tickets")
    }

    /**
     * The internal class TicketAdapter, setup for the listView.
     * @param context The context of the adapter.
     * @param tickets The list of tickets to fill into the listView.
     */
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

            statusView.setImageResource(if (t.status == "Open") R.drawable.ok else R.drawable.notok)

            return resView
        }
    }

}