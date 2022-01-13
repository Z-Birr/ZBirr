package com.matewos.z_birr

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class FormValidationViewModel: ViewModel() {
    val phoneNumber = MutableLiveData<String>("")
    var validPhone = MediatorLiveData<Boolean>().apply {
        addSource(phoneNumber) {
            value = isPhoneNumberValid(it)
        }
    }

    val firstName = MutableLiveData<String>("")
    val lastName = MutableLiveData<String>("")

    val password = MutableLiveData<String>("")
    val confirmPassword = MutableLiveData<String>("")

    var validPassword = MediatorLiveData<Boolean>().apply {
        addSource(password){
            value = isPasswordValid(it)
        }
    }

    var correctPassword = MediatorLiveData<Boolean>().apply {
        addSource(password){
            value = isPasswordCorrect()
        }
    }

    private fun isPasswordCorrect(): Boolean {
        if (SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE).getString("Token", "") != ""){
            return true
        }
        return false
    }

    fun isPasswordValid(pass: String): Boolean {
        return Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{6,}$", pass)
    }

    var validName = MediatorLiveData<Boolean>().apply {
        addSource(firstName){
            value = isNameValid(it)
        }
    }

    fun isPhoneNumberValid(phone: String): Boolean{
        //validPhone.value = Pattern.matches("^\\+\\d{12,}$", phone)
        return Pattern.matches("^\\+\\d{12,}$", phone)
    }
    fun isNameValid(name: String): Boolean{
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z]).{4,}$", name)
    }
}