package com.example.supportbuddy_androidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.supportbuddy_androidapp.data.LoginRepo
import com.example.supportbuddy_androidapp.data.callback.ILoginCallback
import com.example.supportbuddy_androidapp.data.models.AuthUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var loginRepo: LoginRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.activity_login)

        LoginRepo.initialize(this)

        loginRepo = LoginRepo.get()

        loginSubmit.setOnClickListener {
            val username = loginName.text.toString()
            val password = loginPassword.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            startMainActivity(intent,username,password)
        }
    }

    private fun startMainActivity(intent: Intent, username: String, password: String){
        loginRepo = LoginRepo.get()
        val newBundle = Bundle()
        loginRepo.authenticateLogin(object: ILoginCallback {
            override fun onAuthReady(authUser: AuthUser) {
                newBundle.putString("authUserStatus", authUser.status)
                newBundle.putString("basicAuthString", authUser.basicAuthString)
                loginRepo.setAuthString(authUser.basicAuthString!!)
                intent.putExtras(newBundle)
                startActivity(intent)
            }

            override fun onAuthFailure() {
                showWrongCredentialsError()
            }
        },username,password)
    }

    private fun showWrongCredentialsError() {
        Toast.makeText(this, "Username and/or password is wrong!", Toast.LENGTH_SHORT).show()
    }
}