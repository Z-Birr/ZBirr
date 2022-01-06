package com.matewos.z_birr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
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
import com.matewos.z_birr.databinding.FragmentEditNameBinding
import org.json.JSONObject

class EditName : Fragment() {

    lateinit var binding : FragmentEditNameBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    val viewModel: FormValidationViewModel by lazy {
        ViewModelProvider(this).get(FormValidationViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditNameBinding.inflate(inflater, container, false)
        database = Firebase.database.reference
        auth = Firebase.auth


        binding.next.setOnClickListener {

            val url = "http://127.0.0.1:5000/rest-auth/user/"
            val jsonObject = JSONObject()
            jsonObject.put("first_name", binding.editTextTextFirstName.text.toString())
            jsonObject.put("last_name", binding.editTextTextLastName.text.toString())
            jsonObject.put("username", auth.currentUser?.uid)
            Log.i("Backend request", jsonObject.toString())

            val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
            val token = sharedPref?.getString("Token", "")
            Log.i("Backend", token.toString())

            database.child("users").child(auth.currentUser!!.uid).child("first_name").setValue(binding.editTextTextFirstName.text.toString())
            database.child("users").child(auth.currentUser!!.uid).child("last_name").setValue(binding.editTextTextLastName.text.toString())
                .addOnSuccessListener {
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.PUT, url, jsonObject,
                        Response.Listener { response ->
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            with(sharedPref?.edit()) {
                                this?.putString("state", "alreadySignedinUser")
                                this?.apply()
                            }
                            Log.i("Backend", "Response: %s".format(response.toString()))
                        },
                        Response.ErrorListener { error ->
                            Log.i("Backend", "Response: %s".format(error.toString()))
                            SendRequest.respons = JSONObject()
                            SendRequest.respons.put("error", error.toString())
                        }
                    ) {
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val params: MutableMap<String, String> = HashMap()
                            params["Authorization"] = "Token $token"
                            //..add other headers
                            return params
                        }
                    }
                    MySingleton.getInstance(SplashScreen.instance.applicationContext).addToRequestQueue((jsonObjectRequest))


                }
                .addOnFailureListener {  }
        }

        binding.viewModel = viewModel
        // 4. Set the binding's lifecycle (otherwise Live Data won't work properly)
        binding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("firstName", binding.editTextTextFirstName.toString())
        outState.putString("lastName", binding.editTextTextLastName.toString())

    }

}