package com.matewos.z_birr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.databinding.FragmentEditNameBinding

class EditName : Fragment() {

    lateinit var binding : FragmentEditNameBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditNameBinding.inflate(inflater, container, false)
        database = Firebase.database.reference
        auth = Firebase.auth

        binding.next.setOnClickListener {
            database.child("users").child(auth.currentUser!!.uid).child("first_name").setValue(binding.editTextTextFirstName.text.toString())
            database.child("users").child(auth.currentUser!!.uid).child("last_name").setValue(binding.editTextTextLastName.text.toString())
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_editName2_to_signInFragment)
                }
                .addOnFailureListener {  }
        }


        // Inflate the layout for this fragment
        return binding.root
    }

}