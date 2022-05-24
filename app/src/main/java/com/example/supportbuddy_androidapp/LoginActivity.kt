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

    /**
     * Method that is ran when the activity runs.
     * @param savedInstanceState Bundle received by the method that runs the activity. Containing information.
     */
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

    /**
     * Method that requests authentication in the repo, also the method that runs the MainActivity if the typed information about the user fits.
     * @param intent Contains information about the context and which class activity to run.
     * @param username Is the text that the user typed into the username text field on the login screen.
     * @param password Is the text that the user typed into the password text field on the login screen.
     */
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

    /**
     * Method that shows a Toast message whenever the user types in the wrong login information.
     */
    private fun showWrongCredentialsError() {
        Toast.makeText(this, "Username and/or password is wrong!", Toast.LENGTH_SHORT).show()
    }
}