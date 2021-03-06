package com.matewos.z_birr

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    lateinit var binding: FragmentEditNameBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    val viewModel: FormValidationViewModel by lazy {
        ViewModelProvider(this).get(FormValidationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditNameBinding.inflate(inflater, container, false)
        database = Firebase.database.reference
        auth = Firebase.auth


        binding.next.setOnClickListener {
            binding.next.isEnabled = false
            val url = "$BASEURL/rest-auth/user/"
            val jsonObject = JSONObject()
            jsonObject.put("first_name", binding.editTextTextFirstName.text.toString())
            jsonObject.put("last_name", binding.editTextTextLastName.text.toString())
            jsonObject.put("username", auth.currentUser?.uid)

            val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
            val token = sharedPref?.getString("Token", "")

            database.child("users").child(auth.currentUser!!.uid).child("first_name")
                .setValue(binding.editTextTextFirstName.text.toString())
            database.child("users").child(auth.currentUser!!.uid).child("last_name")
                .setValue(binding.editTextTextLastName.text.toString())
                .addOnSuccessListener {
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.PUT, url, jsonObject,
                        Response.Listener { _ ->
                            val sharedPrefState = SplashScreen.instance.getSharedPreferences(
                                STATE,
                                Context.MODE_PRIVATE
                            )
                            with(sharedPrefState?.edit()) {
                                this?.putString("state", "oldUserPasswordSetup")
                                this?.apply()
                            }
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        },
                        Response.ErrorListener { error ->
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
                    MySingleton.getInstance(SplashScreen.instance.applicationContext)
                        .addToRequestQueue((jsonObjectRequest))


                }
                .addOnFailureListener { binding.next.isEnabled = true }
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