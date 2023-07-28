package com.example.proyectokotlin.models.res

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyectokotlin.models.Favorites
import com.example.proyectokotlin.models.Locations
import com.google.firebase.firestore.FirebaseFirestore

class Repo {

    fun getLocationData(): LiveData<MutableList<Locations>>{
        val mutableData = MutableLiveData<MutableList<Locations>>()
        FirebaseFirestore.getInstance().collection("locations").get().addOnSuccessListener { result ->
            val listData = mutableListOf<Locations>()

            for(document in result ){
                val img = document.getString("img")
                val info = document.getString("info")
                val name = document.getString("name")
                val locations = Locations(img!!,info!!,name!!)
                listData.add(locations)
            }
            mutableData.value = listData
        }
        return mutableData
    }

    fun getFavoritesData(session: String): LiveData<MutableList<Favorites>>{
        val mutableData = MutableLiveData<MutableList<Favorites>>()
        FirebaseFirestore.getInstance().collection("users").document(session).collection("favorites").get().addOnSuccessListener { result ->
            val listData = mutableListOf<Favorites>()

            for(document in result ){
                val img = document.getString("img")
                val info = document.getString("info")
                val name = document.getString("name")
                val fav = document.getBoolean("favorite")
                val favorites = Favorites(img!!,info!!,name!!, fav!!)
                listData.add(favorites)
            }
            mutableData.value = listData
        }
        return mutableData
    }
}

