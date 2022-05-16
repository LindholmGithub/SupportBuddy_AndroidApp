package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.supportbuddy_androidapp.LoginActivity
import com.example.supportbuddy_androidapp.MainActivity
import com.example.supportbuddy_androidapp.data.callback.ILoginCallback
import com.example.supportbuddy_androidapp.data.dto.AuthDTO_Out
import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity

class LoginRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/Auth/"
    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    fun authenticateLogin(callback: ILoginCallback,username: String, password: String){
        val authDto = AuthDTO_Out(username,password)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val jsonTicket: String = gson.toJson(authDto)
        val entity = StringEntity(jsonTicket)
        httpClient.post(context, url, entity, "application/json", object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                callback.onAuthReady()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d(MainActivity.TAG, "Failure in authentication. StatusCode = $statusCode")
            }
        })
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