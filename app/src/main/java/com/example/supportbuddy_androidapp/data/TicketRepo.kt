package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.supportbuddy_androidapp.MainActivity
import com.example.supportbuddy_androidapp.data.callback.ICallback
import com.example.supportbuddy_androidapp.data.callback.ITicketCallback
import com.example.supportbuddy_androidapp.data.dto.TicketDTO_Out
import com.example.supportbuddy_androidapp.data.models.Ticket
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONException
import java.lang.Exception


class TicketRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/Ticket"

    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    private val loginRepo: LoginRepo = LoginRepo.get()

    init {
        Log.d("TAG", loginRepo.getAuthString())
        //httpClient.addHeader()
    }

    /**
     * Makes a HTTP GET request for all Tickets to defined api url.
     *
     * @param callback The callback interface in which the method is overridden in the activity that calls for it.
     *
     */
    fun getAll(callback: ICallback){
        httpClient.removeHeader("Authorization")
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

    /**
     * Makes a HTTP GET request for a specific Ticket, based on the tickets ID, to defined api url.
     *
     * @param id The ID of the ticket to base the GET request on.
     * @param callback The callback interface in which the method is overridden in the activity that calls for it.
     *
     */
    fun getTicketById(id: Int, callback: ITicketCallback) {
        httpClient.removeHeader("Authorization")
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

    /**
     * Makes a HTTP POST request to add a ticket, to defined api url.
     *
     * @param newSubject The subject of the ticket that was submitted.
     * @param newMessage The message of the ticket that was submitted.
     * @param newEmail The email of the user that submitted the ticket.
     * @param newFirstName The first name of the user that submitted the ticket.
     * @param newLastName The last name of the user that submitted the ticket.
     * @param newPhone The phone number of the user that submitted the ticket.
     *
     *
     */
    fun addTicket(newSubject: String, newMessage: String, newEmail: String, newFirstName: String, newLastName: String, newPhone: Int){
        httpClient.removeHeader("Authorization")
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
    }

    /**
     * Makes a HTTP POST request to add an answer for a specific Ticket, based on the tickets ID, to defined api url.
     *
     * @param id The ID of the ticket to base the POST request on.
     * @param message The message that the user wrote as an answer.
     * @param attachmentId The ID of the attachment that was added to the answer.
     *
     */
    fun addTicketAnswer(id: Int, message: String, attachmentId: Int): Any {
        val jsonMap : MutableMap<String, String> = mutableMapOf()
        jsonMap.put("message", message)
        if(attachmentId > 0)
            jsonMap.put("attachmentId", attachmentId.toString())
        val gson = Gson()
        val jsonString : String = gson.toJson(jsonMap)
        val entity = StringEntity(jsonString)
        httpClient.addHeader("Authorization", "Basic " + loginRepo.getAuthString())
        httpClient.post(context, url + "/" + id, entity, "application/json", object : AsyncHttpResponseHandler(){
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
                Log.d(MainActivity.TAG, "Failure in addTicketAnswer statusCode = $statusCode")
            }
        })
        return message
    }


    /**
     * Creates an instance of a Ticket object, based on the response from the HTTP request.
     *
     * @param jsonString The data received from the backend, as a JSON string.
     * @return Ticket based on the converted JSON string data.
     */
    private fun getTicketFromString(jsonString: String?): Ticket {
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

    /**
     * Creates a List of ticket objects, based on the response from the HTTP request.
     *
     * @param jsonString The data received from the backend, as a JSON string.
     * @return ticketList based on the converted JSON string data.
     */
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

    /**
     * Makes a HTTP POST request to close a ticket based on the ticket ID.
     *
     * @param id The ID of the ticket that was requested to be closed.
     */
    fun closeTicket(id: Int){
        httpClient.removeHeader("Authorization")
        httpClient.post(url + "/" + id + "/close", object : AsyncHttpResponseHandler(){ override fun onSuccess(
            statusCode: Int,
            headers: Array<out Header>?,
            responseBody: ByteArray?,
        ) {}
            override fun onFailure(
            statusCode: Int,
            headers: Array<out Header>?,
            responseBody: ByteArray?,
            error: Throwable?
        ) {
            Log.d(MainActivity.TAG, "Failure in closeTicket statusCode = $statusCode")
        }
        })
    }

    /**
     * Makes a HTTP DELETE request to delete a ticket based on the ticket ID.
     *
     * @param id The ID of the ticket that was requested to be deleted.
     */
    fun deleteTicket(id: Int) {
        httpClient.removeHeader("Authorization")
        httpClient.delete(context, url + "/" + id, object : AsyncHttpResponseHandler(){
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
                Log.d(MainActivity.TAG, "Failure in deleteTicket statusCode = $statusCode")
            }
        })
    }

    /**
     * Singleton methods to access class when called for.
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: TicketRepo? = null

        fun initialize(context: Context) {
            if (Instance == null)
                Instance = TicketRepo(context)
        }

        fun get(): TicketRepo {
            if (Instance != null) return Instance!!
            throw IllegalStateException("Ticket Repository not initialized")
        }
    }
}