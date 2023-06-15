package com.example.proyectokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {


    private lateinit var rvLocations: RecyclerView
    private lateinit var adapter: MainAdapter

    private lateinit var locationsData: Locations



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter = MainAdapter(this)
        rvLocations = findViewById(R.id.rvLocations)



        rvLocations.layoutManager = LinearLayoutManager(this)
        rvLocations.adapter = adapter

        val dummyList = mutableListOf<Locations>()
        dummyList.add(Locations("https://firebasestorage.googleapis.com/v0/b/holo-af489.appspot.com/o/paisaje1.png?alt=media&token=d5044efe-687b-42dc-9c5f-175167580a3e",
        "Lorem ipsum dolor sit amet consectetur adipiscing elit, iaculis tristique eget pellentesque fames enim, himenaeos dignissim eros curae faucibus per.",
        "Imagen"))
        dummyList.add(Locations("https://firebasestorage.googleapis.com/v0/b/holo-af489.appspot.com/o/paisaje1.png?alt=media&token=d5044efe-687b-42dc-9c5f-175167580a3e",
            "Lorem ipsum dolor sit amet consectetur adipiscing elit, iaculis tristique eget pellentesque fames enim, himenaeos dignissim eros curae faucibus per.",
            "Imagen"))
        dummyList.add(Locations("https://firebasestorage.googleapis.com/v0/b/holo-af489.appspot.com/o/paisaje1.png?alt=media&token=d5044efe-687b-42dc-9c5f-175167580a3e",
            "Lorem ipsum dolor sit amet consectetur adipiscing elit, iaculis tristique eget pellentesque fames enim, himenaeos dignissim eros curae faucibus per.",
            "Imagen"))

        adapter.setListData(dummyList)
        adapter.notifyDataSetChanged()

        //getImage()

    }


    /*private fun getImage(){
        var dbLocation = FirebaseFirestore.getInstance()
        dbLocation.collection("locations").document("Guanajuato")
            .get()
            .addOnSuccessListener {document->
                if (document.data != null){
                    val location = document.toObject(Locations::class.java)
                    locationsData = location!!

                }

                tvPrueba.setText(locationsData.info.toString())

                Picasso.get()
                    .load(locationsData.img.toString())
                    .resize(540,650)
                    .into(imgLocation)

            }
    }*/
}