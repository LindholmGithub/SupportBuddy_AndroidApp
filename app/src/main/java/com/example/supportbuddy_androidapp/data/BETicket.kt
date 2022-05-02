package com.example.supportbuddy_androidapp.data

import org.json.JSONObject

class BETicket(val id: Int, val firstname: String) {

    constructor(jsonObject: JSONObject) :
            this(Integer.parseInt(jsonObject["id"] as String),
            jsonObject["firstname"] as String)

    public override fun toString(): String {
        return "${id}, ${firstname}"
    }
}