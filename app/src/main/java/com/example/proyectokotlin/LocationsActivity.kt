package com.example.proyectokotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class LocationsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var ivImgL: ImageView
    private lateinit var tvInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        tvName = findViewById(R.id.tvName)
        ivImgL = findViewById(R.id.ivImgL)
        tvInfo = findViewById(R.id.tvInfo)

        setData()


    }

    private fun setData(){
        val bundle: Bundle? = intent.extras
        val img = bundle!!.getString("img")
        val info = bundle!!.getString("info")
        val name = bundle!!.getString("name")

        tvName.text = name
        tvInfo.text = info
        Glide.with(this).load(img).into(ivImgL)

    }

}