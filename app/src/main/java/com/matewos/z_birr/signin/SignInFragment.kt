package com.matewos.z_birr.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
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

    val viewModel: FormValidationViewModel by activityViewModels()
    val sharedPrefState = SplashScreen.instance.applicationContext.getSharedPreferences(
        STATE, Context.MODE_PRIVATE)
    var newUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

// 3. Set the viewModel instance
        binding.viewModel = viewModel
        // 4. Set the binding's lifecycle (otherwise Live Data won't work properly)
        binding.lifecycleOwner = this

        auth = Firebase.auth
        database = Firebase.database.reference
        var url: String
        val jsonObject = JSONObject()
        jsonObject.put("username", auth.currentUser!!.uid)
        val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)

        val sharedPrefState = SplashScreen.instance.getSharedPreferences(STATE, Context.MODE_PRIVATE)
        with(sharedPrefState?.edit()) {
            this?.putString("uid", auth.currentUser!!.uid)
            this?.apply()
        }

        val user = User("n", null, null)
        database.child("users").child(auth.currentUser!!.uid).child("last_name").get()
            .addOnSuccessListener {
                if (it.value == null || it.value == "n") {
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
            binding.finish.isEnabled = false
            binding.progressBar2.visibility = View.VISIBLE
            if (newUser) {
                jsonObject.put("password1", binding.editTextTextPassword.text.toString())
                jsonObject.put("password2", binding.editTextTextConfirmPassword.text.toString())
                url = "$BASEURL/rest-auth/registration/"
            } else {
                jsonObject.put("password", binding.editTextTextPassword.text.toString())
                url = "$BASEURL/rest-auth/login/"
            }
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->

                    try {
                        with(sharedPref?.edit()) {
                            this?.putString("Token", response.getString("key"))
                            this?.apply()
                            viewModel.correctPassword.value = true
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        viewModel.correctPassword.value = false
                    }
                    try {
                        val token = response.getString("key")

                        if (newUser) {
                            url = "$BASEURL/initialize/"
                            SendRequest.authorized(
                                token, Request.Method.POST, url, null
                            )

                            with(sharedPrefState?.edit()) {
                                this?.putString("state", "newUserPasswordSetup")
                                this?.apply()
                            }
                            findNavController().navigate(R.id.action_signInFragment_to_editName2)
                        }
                        else {
                            with(sharedPrefState?.edit()) {
                                this?.putString("state", "oldUserPasswordSetup")
                                this?.apply()
                            }
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            binding.finish.isEnabled = false
                            binding.progressBar2.visibility = View.VISIBLE


                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        binding.finish.isEnabled = true
                        binding.progressBar2.visibility = View.GONE
                        Toast.makeText(context, "Password incorrect", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    binding.finish.isEnabled = true
                    binding.progressBar2.visibility = View.GONE
                    Toast.makeText(context, "Make sure you are connected to stable internet connection", Toast.LENGTH_SHORT).show()
                }
            )
            MySingleton.getInstance(SplashScreen.instance.applicationContext)
                .addToRequestQueue((jsonObjectRequest))


        }


        return binding.root
    }


}