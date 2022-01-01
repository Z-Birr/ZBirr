package com.matewos.z_birr.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.*
import com.matewos.z_birr.databinding.FragmentSignInBinding
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest

class SignInFragment : Fragment() {

    val TAG = SignInFragment::class.qualifiedName
    companion object{
        const val USER_ID = "user_id"
    }
    lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var newUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        database = Firebase.database.reference







        val user = User("n", null, null)
        database.child("users").child(auth.currentUser!!.uid).child("first_name").get()
            .addOnSuccessListener {
                if (it.value == null) {
                    database.child("users").child(auth.currentUser!!.uid).setValue(user)
                        .addOnSuccessListener {
                            newUser = true
                        }
                        .addOnFailureListener {  }
                }
                else {
                    binding.editTextTextConfirmPassword.visibility = View.GONE
                }
            }
            .addOnFailureListener{  }












        binding.finish.setOnClickListener {
            val baseUrl = "http://127.0.0.1:5000"
            var url=""
            val jsonObject = JSONObject()
            jsonObject.put("username", auth.currentUser!!.uid.toString())
            url = if (newUser) {
                jsonObject.put("password1", binding.editTextTextPassword.text.toString())
                jsonObject.put("password2", binding.editTextTextConfirmPassword.text.toString())
                "$baseUrl/rest-auth/registration/"
            } else {
                jsonObject.put("password", binding.editTextTextPassword.text.toString())
                "$baseUrl/rest-auth/login/"
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    val sharedPref =
                        SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
                    try {
                        val token = response.getString("key")

                        if (newUser) {
                            url = "$baseUrl/initialize/"
                            val response2 = SendRequest.authorized(
                                token, Request.Method.POST, url, null
                            )
                            Log.i(
                                "Backend response  ",
                                response2.toString() + "Second" + SplashScreen.instance.getSharedPreferences(
                                    TOKEN,
                                    Context.MODE_PRIVATE
                                ).getString("Token", "")
                            )

                        }

                        with(sharedPref?.edit()) {
                            this?.putString("Token", token)
                            this?.apply()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i("Backend", "Response: %s".format(response.toString()))
                },
                { error ->
                    Log.i("Backend", "Response: %s".format(error.toString()))

                }
            )
            MySingleton.getInstance(SplashScreen.instance.applicationContext)
                .addToRequestQueue((jsonObjectRequest))

            val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
//
//                try {
//                    with(sharedPref?.edit()) {
//                        this?.putString("Token", "Token " + response.getString("key"))
//                        this?.apply()
//                    }
//                }
//                catch (e: JSONException){
//                    e.printStackTrace()
//                }

                //val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
                if (newUser) {
                    findNavController().navigate(R.id.action_signInFragment_to_editName2)
                }
                else {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

        }
        return binding.root
    }

    fun sha256(input: String) = hashString("SHA-256", input)

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }
        return result.toString()
    }

}