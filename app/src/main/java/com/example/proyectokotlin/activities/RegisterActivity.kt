package com.example.proyectokotlin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyectokotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {


    private var name by Delegates.notNull<String>()
    private var lName by Delegates.notNull<String>()
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var etLName: EditText
    private lateinit var btRegister: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etLName = findViewById(R.id.etLname)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btRegister = findViewById(R.id.btRegister)

        btRegister.setOnClickListener{
            registerUser()
        }

    }

    private fun registerUser(){

        name = etName.text.toString()
        lName = etLName.text.toString()
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if ((name.isEmpty())||(lName.isEmpty())||(email.isEmpty())||(password.isEmpty())){
            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
        }
        else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{

                    if(it.isSuccessful){
                        var dataRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                        var dbRegister = FirebaseFirestore.getInstance()

                        dbRegister.collection("users").document(email).set(hashMapOf(
                            "name" to name,
                            "lastName" to lName,
                            "email" to email,
                            "registerDate" to dataRegister
                        ))
                        goMain(email, ProviderType.EMAIL_PASSWORD)
                    }
                    else Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
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