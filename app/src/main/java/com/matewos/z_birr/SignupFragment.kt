package com.matewos.z_birr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.databinding.FragmentSignupBinding
import java.util.concurrent.TimeUnit


class SignupFragment : Fragment() {

    private val TAG: String? = SignupFragment::class.qualifiedName
    private var _binding: FragmentSignupBinding? = null
    private lateinit var auth: FirebaseAuth
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //    private lateinit var binding: FragmentSignupBinding
    val viewModel: FormValidationViewModel by lazy {
        ViewModelProvider(this).get(FormValidationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        // 3. Set the viewModel instance
        binding.viewModel = viewModel
        // 4. Set the binding's lifecycle (otherwise Live Data won't work properly)
        binding.lifecycleOwner = this

        binding.editTextPhone.setText(savedInstanceState?.getString("phoneNumber"))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUp.setOnClickListener {
            //VIA PHONE NUMBER
            binding.progressBar.visibility = View.VISIBLE
            binding.textCheckRobot.visibility = View.VISIBLE
            binding.buttonSignUp.isEnabled = false

            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:$credential")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e)

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                    }

                    // Show a message and update the UI
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.wtf(TAG, "onCodeSent:$verificationId")

                    savedInstanceState?.putString(VERIFICATION_ID, storedVerificationId)

                    storedVerificationId = if (verificationId != "") {
                        verificationId
                    } else {
                        savedInstanceState?.getString(VERIFICATION_ID).toString()
                    }

                    val bundle = Bundle()
                    bundle.putString(VERIFICATION_ID, storedVerificationId)
                    if (isAdded()){
                        findNavController().navigate(
                            R.id.action_signupFragment_to_verificationFragment,
                            bundle
                        )
                    }

                    // Save verification ID and resending token so we can use them later

//                    var storedVerificationId = verificationId
//                    var resendToken = token


                }
            }

            val phoneNumber = binding.editTextPhone.text.toString()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putString("firstName", binding.editTextTextFirstName.toString())
//        outState.putString("lastName", binding.editTextTextLastName.toString())
//        outState.putString("email", binding.editTextTextEmailAddress.toString())
        outState.putString("phoneNumber", binding.editTextPhone.toString())
//        outState.putString("password", binding.editTextTextPassword.toString())
//        outState.putString("confirmPassword", binding.editTextTextConfirmPassword.toString())
//        outState.putString("verificationCode", binding.editTextNumberVerificationCode.text.toString())
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


}
