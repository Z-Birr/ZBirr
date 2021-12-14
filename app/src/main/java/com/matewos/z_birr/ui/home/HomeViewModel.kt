package com.matewos.z_birr.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    private val database: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth


    val text: LiveData<String> = _text
    val balance: LiveData<Double> = MutableLiveData<Double>().apply {
        value = getData("balance") as Double
    }
    val firstName : LiveData<String> = MutableLiveData<String>().apply {
        value = getData("first_name") as String
    }
    val lastName : LiveData<String> = MutableLiveData<String>().apply {
        value = getData("last_name") as String
    }
    val password : LiveData<String> = MutableLiveData<String>().apply {
        value = getData("first_name") as String
    }
    //constructor()
     private fun getData(arg: String) : Any?{
        var value: Any? = null
        database.child("users").child(auth.currentUser!!.uid).child(arg).get()
            .addOnSuccessListener {
                value = it.value
            }
        return value
    }
}