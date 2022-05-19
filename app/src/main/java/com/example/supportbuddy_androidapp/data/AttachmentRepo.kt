package com.example.supportbuddy_androidapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.JsonReader
import com.example.supportbuddy_androidapp.data.dto.AttachmentDTO_Out
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileNotFoundException

class AttachmentRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/attachment/"
    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    fun addAttachment(attachmentUrl: String) : AttachmentDTO_Out{
        var attachmentDtoOut = AttachmentDTO_Out(0,"")

        val attachmentFile = File(attachmentUrl)
        val params = RequestParams()

        try {
            params.put("file", attachmentFile)
            params.put("filename", attachmentFile.name)
        } catch(e : FileNotFoundException) {}

        params.setForceMultipartEntityContentType(true)

        httpClient.post(context, url, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONArray?
            ) {
                super.onSuccess(statusCode, headers, response)

                val parsedJson = JSONTokener(response.toString()).nextValue() as JSONObject

                val attachmentId = parsedJson.getInt("id")
                val attachmentWebUrl = parsedJson.getString("url")

                val newAttachment = AttachmentDTO_Out(attachmentId, attachmentWebUrl)

                attachmentDtoOut = newAttachment

            }
        })

        return attachmentDtoOut
    }

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