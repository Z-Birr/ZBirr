package com.matewos.z_birr.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private lateinit var database: DatabaseReference
    //constructor()
    public fun getUserData() : Unit{
        database = FirebaseDatabase.getInstance().reference
        val ref = database.ref



    }
}