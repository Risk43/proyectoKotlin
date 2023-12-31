package com.example.proyectokotlin.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectokotlin.models.res.Repo

class MainViewModel: ViewModel() {

    val repo = Repo()

    fun fetchLocationData(): LiveData<MutableList<Locations>>{
        val mutableData = MutableLiveData<MutableList<Locations>>()
        repo.getLocationData().observeForever(){ locationList->
            mutableData.value = locationList
        }
        return mutableData
    }

    fun fetchFavoriteData(session: String): LiveData<MutableList<Favorites>>{
        val mutableData = MutableLiveData<MutableList<Favorites>>()
        repo.getFavoritesData(session).observeForever(){ favoriteList->
            mutableData.value = favoriteList
        }
        return mutableData
    }
}