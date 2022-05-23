package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.supportbuddy_androidapp.LoginActivity
import com.example.supportbuddy_androidapp.MainActivity
import com.example.supportbuddy_androidapp.data.callback.ILoginCallback
import com.example.supportbuddy_androidapp.data.dto.AuthDTO_Out
import com.example.supportbuddy_androidapp.data.models.AuthUser
import com.example.supportbuddy_androidapp.data.models.Ticket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONException
import java.lang.Exception

class LoginRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/Auth/"
    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    private var authString = ""

    fun authenticateLogin(callback: ILoginCallback,username: String, password: String){
        val authDto = AuthDTO_Out(username,password)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val jsonTicket: String = gson.toJson(authDto)
        val entity = StringEntity(jsonTicket)
        httpClient.post(context, url + "authenticate", entity, "application/json", object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val authUser: AuthUser = getAuthUserFromString(String(responseBody!!))
                callback.onAuthReady(authUser)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                callback.onAuthFailure()
            }
        })
    }

    private fun getAuthUserFromString(jsonString: String?): AuthUser {
        if (jsonString!!.startsWith("error")) {
            Log.d(MainActivity.TAG, "Error: $jsonString")
        }
        if (jsonString == null){
            Log.d(MainActivity.TAG, "Error: NO RESULT")
        }

        try {
            return Gson().fromJson(jsonString, AuthUser::class.java)

        } catch (e: JSONException){
            e.printStackTrace()
        }
        throw Exception("Error Exception")
    }

    public fun setAuthString(str : String) {
        authString = str
    }

    public fun getAuthString() : String {
        return authString
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: LoginRepo? = null

        fun initialize(context: Context) {
            if (Instance == null)
                Instance = LoginRepo(context)
        }

        fun get(): LoginRepo {
            if (Instance != null) return Instance!!
            throw IllegalStateException("Login Repository not initialized")
        }
    }

}