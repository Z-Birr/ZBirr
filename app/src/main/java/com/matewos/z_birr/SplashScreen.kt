package com.matewos.z_birr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.signin.SignInFragment

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplashScreen.instance = this
        val auth = FirebaseAuth.getInstance()
        Firebase.database.setPersistenceEnabled(true)
        Firebase.database.reference.keepSynced(true)
        if (auth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(SignInFragment.USER_ID, auth.currentUser!!.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    companion object{
        lateinit var instance: SplashScreen
        fun getApplication(): SplashScreen{
            return instance
        }
    }
}