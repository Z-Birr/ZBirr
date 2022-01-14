package com.matewos.z_birr.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.BASEURL
import com.matewos.z_birr.SendRequest
import com.matewos.z_birr.SplashScreen
import com.matewos.z_birr.TOKEN
import org.json.JSONException

class HomeViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    private val database: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth

    val text: LiveData<String> = _text

    private val _firstName: MutableLiveData<String> = MutableLiveData<String>().apply {
        database.child("users").child(auth.currentUser!!.uid).child("first_name").get()
            .addOnSuccessListener {
                value = it.value as String
                Log.i("ViewModel", "get data successful: ${value}")
            }
            .addOnFailureListener {
                Log.e("ViewModel", it.message.toString())
            }
    }
    val _lastName: MutableLiveData<String> = MutableLiveData<String>().apply {
        database.child("users").child(auth.currentUser!!.uid).child("last_name").get()
            .addOnSuccessListener {
                //value = it.value as String
                Log.i("ViewModel", "get data successful: ${value}")
            }
            .addOnFailureListener {
                Log.e("ViewModel", it.message.toString())
            }
    }


    val firstName: LiveData<String> = _firstName
    val lastName: LiveData<String> = _lastName

    //    init {
//        Firebase.database.setPersistenceEnabled(true)
//        database.keepSynced(true)
//    }
    private fun getData(arg: String): Any? {
        var value: Any? = null
        Log.i("ViewModel", "get data called")
        database.child("users").child(auth.currentUser!!.uid).child(arg).get()
            .addOnSuccessListener {
                value = it.value
                Log.i("ViewModel", "get data successful: ${value}")
            }
            .addOnFailureListener {
                Log.e("ViewModel", it.message.toString())
            }
        Log.i("ViewModel", "-----------------${value}")
        return value
    }
}