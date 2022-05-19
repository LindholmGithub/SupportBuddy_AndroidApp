package com.example.supportbuddy_androidapp.data

import android.content.Context
import com.example.supportbuddy_androidapp.data.dto.AttachmentDTO_Out
import com.loopj.android.http.AsyncHttpClient

class AttachmentRepo constructor(private val context: Context) {

    private val url = "http://vps.qvistgaard.me:8980/api/attachment/"
    private val httpClient: AsyncHttpClient = AsyncHttpClient()

    fun addAttachment(attachmentUrl: String) : AttachmentDTO_Out{
        val attachmentDtoOut = AttachmentDTO_Out(0,"")
        return attachmentDtoOut
    }

}