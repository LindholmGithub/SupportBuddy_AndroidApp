package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.StrictMode
import com.example.supportbuddy_androidapp.data.dto.AttachmentDTO_Out
import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.client.HttpClient
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.entity.mime.content.FileBody
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.message.BasicNameValuePair
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI

class AttachmentRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/attachment/"
    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    private val policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()

    /**
     * Makes a HTTP POST request to defined api url.
     *
     * @param attachmentUrl Url to the image taken with the built-in camera.
     * @return AttachmentDTO_Out based on the response from the API.
     */
    fun addAttachment(attachmentUrl: String) : AttachmentDTO_Out{
        StrictMode.setThreadPolicy(policy)

        var attachmentDtoOut = AttachmentDTO_Out(0,"")

        val customHttpClient : HttpClient = DefaultHttpClient()
        val customHttpPost : HttpPost = HttpPost(url)

        val attachmentFile = File(attachmentUrl)

        val nameValuePairs : MutableList<BasicNameValuePair> = ArrayList<BasicNameValuePair>()
        nameValuePairs.add(BasicNameValuePair("filename", attachmentFile.name))

        customHttpClient.params.setParameter("Connection","Keep-Alive")
        customHttpClient.params.setParameter("Content-Type", "multipart/form-data")

        val entity : MultipartEntityBuilder = MultipartEntityBuilder.create();
        for(pair in nameValuePairs) {
            entity.addTextBody(pair.name, pair.value)
        }

        entity.addPart("file", FileBody(attachmentFile))

        customHttpPost.entity = entity.build()

        val response : HttpResponse = customHttpClient.execute(customHttpPost)
        val reader : BufferedReader = BufferedReader(InputStreamReader(response.entity.content, "UTF-8"))
        val jsonResponse = reader.readLine()

        val parsedJson = JSONTokener(jsonResponse).nextValue() as JSONObject

        val attachmentId = parsedJson.getInt("id")
        val attachmentWebUrl = parsedJson.getString("url")

        val newAttachment = AttachmentDTO_Out(attachmentId, attachmentWebUrl)

        attachmentDtoOut = newAttachment
        return attachmentDtoOut
    }

    /**
     * Singleton methods to access class when called for.
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: AttachmentRepo? = null

        fun initialize(context: Context) {
            if (Instance == null)
                Instance = AttachmentRepo(context)
        }

        fun get(): AttachmentRepo {
            if (Instance != null) return Instance!!
            throw IllegalStateException("Ticket Repository not initialized")
        }
    }

}