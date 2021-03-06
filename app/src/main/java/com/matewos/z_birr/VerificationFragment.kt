package com.matewos.z_birr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.PhoneAuthProvider
import com.matewos.z_birr.databinding.FragmentVerificationBinding
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class VerificationFragment : Fragment() {
    lateinit var binding: FragmentVerificationBinding
    private val TAG: String? = VerificationFragment::class.qualifiedName
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        database = Firebase.database.reference
        database.keepSynced(true)
        binding.editTextNumberVerificationCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.editTextNumberVerificationCode.text.toString().length >= 6) {
                    binding.progressBar6.visibility = View.VISIBLE
                    val storedVerificationId = arguments?.getString(VERIFICATION_ID)!!
                    val code = binding.editTextNumberVerificationCode.text.toString()
                    binding.editTextNumberVerificationCode
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    binding.progressBar6.visibility = View.GONE
                }
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    if (auth.currentUser != null) {
                        findNavController().navigate(R.id.action_verificationFragment_to_signInFragment)
                        val sharedPref =
                            SplashScreen.instance.applicationContext.getSharedPreferences(
                                STATE, Context.MODE_PRIVATE
                            )
                        with(sharedPref?.edit()) {
                            this?.putString("state", "verified")
                            this?.apply()
                        }
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(context, "Make sure you entered correct verification code", Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                }
            }
    }

}