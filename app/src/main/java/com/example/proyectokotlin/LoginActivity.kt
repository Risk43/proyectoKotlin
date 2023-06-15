package com.example.proyectokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.view.View
import java.lang.Exception
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    private lateinit var tvRegister: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegister= findViewById(R.id.tvRegister)
        etEmail= findViewById(R.id.etEmail)
        etPassword= findViewById(R.id.etPass)
        mAuth = FirebaseAuth.getInstance()

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    fun login(view: android.view.View){
        loginUser()
    }

    private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                    if (task.isSuccessful){
                        goPrincipal()
                    }
                    else Toast.makeText(this, "Error en los datos", Toast.LENGTH_SHORT).show()
                
        }
    }

    private fun goPrincipal(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}