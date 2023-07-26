package com.example.proyectokotlin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.proyectokotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {


    private lateinit var profile: TextView
    private lateinit var profileName: TextView
    private lateinit var profileLName: TextView
    private lateinit var profileEmail: TextView

    private lateinit var ivBack: ImageView
    private lateinit var ivSingOut: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")


        profile = findViewById(R.id.tvProfile)
        profileName = findViewById(R.id.tvProfileName)
        profileLName = findViewById(R.id.tvProfileLName)
        profileEmail = findViewById(R.id.tvProfileEmail)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider)
        prefs.apply()

        val prefsRec = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val session = prefsRec.getString("email", null)


        modules(session!!, ProviderType.EMAIL_PASSWORD)

    }

    fun modules(session: String, provider: ProviderType){
        setData(session)
        goMain(session, provider)
        singOut()
    }


    private fun singOut(){
        ivSingOut = findViewById(R.id.ivSingOut)

        ivSingOut.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun  setData(session: String){
        if (session != null){
            FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { result ->
                for (document in result){
                    if (document.getString("email") == session){
                        val name = document.getString("name")
                        val lName = document.getString("lastName")
                        val email = document.getString("email")

                        profile.text = name
                        profileName.text = name
                        profileLName.text = lName
                        profileEmail.text = email

                    }
                }
            }
        }
    }

    private fun goMain(session: String, provider: ProviderType) {
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", session)
            intent.putExtra("provider", provider.name)
            startActivity(intent)
        }
    }
}