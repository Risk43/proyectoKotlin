package com.example.proyectokotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    val repo = Repo()

    fun fetchLocationData(): LiveData<MutableList<Locations>>{
        val mutableData = MutableLiveData<MutableList<Locations>>()
        repo.getLocationData().observeForever(){ locationList->
            mutableData.value = locationList
        }

        return mutableData
    }

}