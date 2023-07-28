package com.example.proyectokotlin.activities

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.proyectokotlin.R
import com.google.firebase.firestore.FirebaseFirestore

class LocationsActivity : AppCompatActivity() {

    private var favorite: Boolean? = false

    private var session: String? = null

    private lateinit var tvName: TextView
    private lateinit var tvInfo: TextView

    private lateinit var ivImgL: ImageView
    private lateinit var ivSetFavorite: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val bundle: Bundle? = intent.extras
        val img = bundle?.getString("img")
        val info = bundle?.getString("info")
        val name = bundle?.getString("name")
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        favorite = bundle?.getBoolean("favorite")

        session = setSharedPreferences(email,provider)

        initVArs()
        initComponents(img,info,name,session, favorite)
    }

    private fun setSharedPreferences(email: String?, provider: String?): String? {
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        val prefsRec = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        return prefsRec.getString("email", null)
    }

    private fun initVArs(){
        tvName = findViewById(R.id.tvName)
        ivImgL = findViewById(R.id.ivImgL)
        tvInfo = findViewById(R.id.tvInfo)
        ivSetFavorite = findViewById(R.id.ivSetFavorite)
    }

    private fun initComponents(img: String?, info: String?, name: String?, session: String?, fav: Boolean?){
        setData(img,info,name, fav)
        clickActions(session, img, info, name)
    }

    private fun setData(img: String?, info: String?, name: String?, fav: Boolean?){
        if (fav != false){
            ivSetFavorite.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
        }else{
            ivSetFavorite.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.out_line))
        }
        tvName.text = name
        tvInfo.text = info
        Glide.with(this).load(img).into(ivImgL)
    }

    private fun clickActions(session: String?, img: String?, info: String?,name: String?){
        if (favorite != false){
            ivSetFavorite.setOnClickListener {
                ivSetFavorite.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
                registerFavorite(session,img,info,name)
            }
        }else{
            ivSetFavorite.setOnClickListener {
                ivSetFavorite.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.out_line))
                deleteFavorite(session,name)
            }
        }
    }

    private fun registerFavorite(session: String?, img: String?, info: String?, name: String?){
        val name = name
        val info = info
        val img = img
        favorite = true

        FirebaseFirestore.getInstance().collection("users").document(session!!).collection("favorites").document(name!!).set(hashMapOf(
            "img" to img,
            "info" to info,
            "name" to name,
            "favorite" to favorite
        )).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Agregado a favoritos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Error al agregar a favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFavorite(session: String?, name: String?){
        favorite = false
        FirebaseFirestore.getInstance().collection("users").document(session!!).collection("favorites").document(name!!).delete()
    }

}