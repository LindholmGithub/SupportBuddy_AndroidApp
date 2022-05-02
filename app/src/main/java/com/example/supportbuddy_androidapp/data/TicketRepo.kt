package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.supportbuddy_androidapp.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header
import org.json.JSONException

class TicketRepo() {

    private val url = "http://vps.qvistgaard.me:8980/api/Ticket"

    private val httpClient: AsyncHttpClient = AsyncHttpClient()

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
}