package com.example.proyectokotlin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.proyectokotlin.R
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {


    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    private lateinit var logLayout: LinearLayout
    private lateinit var tvRegister: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logLayout = findViewById(R.id.logLayout)
        tvRegister= findViewById(R.id.tvRegister)
        etEmail= findViewById(R.id.etEmail)
        etPassword= findViewById(R.id.etPass)
        mAuth = FirebaseAuth.getInstance()

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        session()
    }


    override fun onStart() {
        super.onStart()
        logLayout.visibility = View.VISIBLE
    }
    fun login(view: android.view.View){
        loginUser()
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null){
            logLayout.visibility = View.INVISIBLE
            goMain(email, ProviderType.valueOf(provider))
        }
    }

    private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if ((email.isEmpty())||(password.isEmpty())){
            Toast.makeText(this, "Error en los datos", Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
                    if (task.isSuccessful){
                        goMain(email, ProviderType.EMAIL_PASSWORD)
                    }
                    else Toast.makeText(this, "Error en los datos", Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun goMain(email: String, provider: ProviderType){
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("provider",provider.name)
        startActivity(intent)
    }
}