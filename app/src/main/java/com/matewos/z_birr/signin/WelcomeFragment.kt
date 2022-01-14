package com.matewos.z_birr.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.matewos.z_birr.MainActivity
import com.matewos.z_birr.R
import com.matewos.z_birr.STATE
import com.matewos.z_birr.SplashScreen
import com.matewos.z_birr.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var resultLauncher : ActivityResultLauncher<Intent>
    val sharedPrefState = SplashScreen.instance.applicationContext.getSharedPreferences(
        STATE, Context.MODE_PRIVATE)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (sharedPrefState?.getString("state", "") == "verified"){
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }
        else if (sharedPrefState.getString("state", "") == "newUserPasswordSetup") {
            findNavController().navigate(R.id.action_welcomeFragment_to_editName2)
        }
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signupFragment)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val response = IdpResponse.fromResultIntent(data)
                if (it.resultCode == Activity.RESULT_OK) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra(SignInFragment.USER_ID, user!!.uid)
                    activity?.startActivity(intent)
                } else {
                    Log.e("TAG", "Sign-in failed", response!!.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}