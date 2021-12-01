package com.matewos.z_birr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.matewos.z_birr.databinding.FragmentSignupBinding
import java.security.MessageDigest
import java.util.concurrent.TimeUnit


class SignupFragment : Fragment() {

    private val TAG: String? = SignupFragment::class.qualifiedName
    private lateinit var signupViewModel: SignupViewModel
    private var _binding: FragmentSignupBinding? = null
    private lateinit var auth: FirebaseAuth
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        signupViewModel = ViewModelProvider(this).get(SignupViewModel::class.java)

        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.editTextTextFirstName.setText(savedInstanceState?.getString("firstName"))
        binding.editTextTextLastName.setText(savedInstanceState?.getString("lastName"))
        binding.editTextTextEmailAddress.setText(savedInstanceState?.getString("email"))
        binding.editTextPhone.setText(savedInstanceState?.getString("phoneNumber"))
        binding.editTextTextConfirmPassword.setText(savedInstanceState?.getString("confirmPassword"))
        binding.editTextTextPassword.setText(savedInstanceState?.getString("password"))
        binding.editTextNumberVerificationCode.setText(savedInstanceState?.getString("verificationCode"))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUp.setOnClickListener {
            //VIA PHONE NUMBER

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

                    if (verificationId != ""){
                        storedVerificationId = verificationId
                    }
                    else{
                        storedVerificationId = savedInstanceState?.getString("verificationId").toString()
                    }

                    storedVerificationId = verificationId
                    savedInstanceState?.putString("verificationId", storedVerificationId)
                    // Save verification ID and resending token so we can use them later

                    binding.verify.setOnClickListener {
                        if (storedVerificationId == ""){
                            storedVerificationId = savedInstanceState?.getString("verificationId").toString()
                        }
                        Log.wtf(TAG, "onCodeSent in verify:$storedVerificationId")
                        val code = binding.editTextNumberVerificationCode.text.toString()
                        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                        signInWithPhoneAuthCredential(credential)
                    }
                    var storedVerificationId = verificationId
                    var resendToken = token


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


            //VIA EMAIL
//            val email = binding.editTextTextEmailAddress.text.toString()
//            val password = binding.editTextTextPassword.text.toString()
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful){
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    val intent = Intent(activity, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    //update ui
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(activity, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    //updateUI(null)
//                }
//            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("firstName", binding.editTextTextFirstName.toString())
        outState.putString("lastName", binding.editTextTextLastName.toString())
        outState.putString("email", binding.editTextTextEmailAddress.toString())
        outState.putString("phoneNumber", binding.editTextPhone.toString())
        outState.putString("password", binding.editTextTextPassword.toString())
        outState.putString("confirmPassword", binding.editTextTextConfirmPassword.toString())
        outState.putString("verificationCode", binding.editTextNumberVerificationCode.text.toString())
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

                    val user = task.result?.user
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
