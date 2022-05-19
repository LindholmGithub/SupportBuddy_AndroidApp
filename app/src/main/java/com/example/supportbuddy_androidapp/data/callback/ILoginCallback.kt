package com.example.supportbuddy_androidapp.data.callback

import com.example.supportbuddy_androidapp.data.models.AuthUser

interface ILoginCallback {
    fun onAuthReady(authUser: AuthUser)
}