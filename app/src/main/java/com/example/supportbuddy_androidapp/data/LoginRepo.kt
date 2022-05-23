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

    /**
     * Makes a HTTP POST request to defined api url.
     *
     * @param callback The callback interface in which the method is overridden in the activity that calls for it.
     * @param username The username input that the user wrote when attempting to login.
     * @param password The password input that the user wrote when attempting to login.
     *
     */
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

    /**
     * Creates an instance of an AuthUser object, based on the response from the HTTP request.
     *
     * @param jsonString The data received from the backend, as a JSON string.
     * @return AuthUser based on the converted JSON string data.
     */
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

    fun setAuthString(str : String) {
        authString = str
    }

    fun getAuthString() : String {
        return authString
    }

    /**
     * Singleton methods to access class when called for.
     */
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