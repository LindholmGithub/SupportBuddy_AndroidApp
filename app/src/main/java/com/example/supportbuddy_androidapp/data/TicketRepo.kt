package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.supportbuddy_androidapp.MainActivity
import com.example.supportbuddy_androidapp.data.dto.TicketDTO_Out
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONException
import java.lang.Exception


class TicketRepo constructor(context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/Ticket"
    private val context = context;

    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    init {
        //httpClient.addHeader()
    }


    fun getAll(callback: ICallback){
        httpClient.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val tickets = getTicketsFromString(String(responseBody!!))
                Log.d(MainActivity.TAG, "Tickets received - ${tickets.size}")
                callback.onTicketsReady( tickets )
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d(MainActivity.TAG, "Failure in getAll statusCode = $statusCode")
            }
        })
    }

    fun getTicketById(id: Int, callback: ITicketCallback) {
        var ticket: Ticket
        httpClient.get(url + "/" + id, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                ticket = getTicketFromString(String(responseBody!!))
                Log.d(MainActivity.TAG, "Ticket received")
                callback.onTicketReady(ticket)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d(MainActivity.TAG, "Failure in getById statusCode = $statusCode")
            }
        })
    }

    fun addTicket(newSubject: String, newMessage: String, newEmail: String, newFirstName: String, newLastName: String, newPhone: Int): Any {
        val newTicket = TicketDTO_Out(newSubject,newMessage,newEmail,newFirstName,newLastName,newPhone)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val jsonTicket: String = gson.toJson(newTicket)
        val entity = StringEntity(jsonTicket)
        httpClient.post(context, url, entity, "application/json", object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {}

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d(MainActivity.TAG, "Failure in addTicket statusCode = $statusCode")
            }
        })
        return newTicket
    }

    private fun getTicketFromString(jsonString: String?): Ticket{
        if (jsonString!!.startsWith("error")) {
            Log.d(MainActivity.TAG, "Error: $jsonString")
        }
        if (jsonString == null){
            Log.d(MainActivity.TAG, "Error: NO RESULT")
        }

        try {
            return Gson().fromJson(jsonString, Ticket::class.java)

        } catch (e: JSONException){
            e.printStackTrace()
        }
        throw Exception("Hej")
    }

    private fun getTicketsFromString(jsonString: String?): List<Ticket>{

        val ticketList = ArrayList<Ticket>()

        if (jsonString!!.startsWith("error")) {
            Log.d(MainActivity.TAG, "Error: $jsonString")
            return ticketList
        }
        if (jsonString == null){
            Log.d(MainActivity.TAG, "Error: NO RESULT")
            return ticketList
        }

        try {
            val typeToken = object : TypeToken<List<Ticket>>() {}.type
            val listOfObjects = Gson().fromJson<List<Ticket>>(jsonString, typeToken)
            ticketList.addAll(listOfObjects)

        } catch (e: JSONException){
            e.printStackTrace()
        }

        return ticketList
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: TicketRepo? = null

        fun initialize(context: Context) {
            if (Instance == null)
                Instance = TicketRepo(context)
        }

        fun get(): TicketRepo {
            if (Instance != null) return Instance!!
            throw IllegalStateException("Friend Repository not initialized")
        }
    }
}