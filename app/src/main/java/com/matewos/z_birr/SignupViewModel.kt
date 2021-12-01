package com.matewos.z_birr

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.matewos.z_birr.databinding.FragmentSignupBinding

class SignupViewModel  : ViewModel(){
    private var _firstName = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    var firstName: LiveData<String> = _firstName

    private lateinit var database: DatabaseReference
    //constructor()
    public fun getUserData() : Unit{
        database = FirebaseDatabase.getInstance().reference
        val ref = database.ref


    }
}