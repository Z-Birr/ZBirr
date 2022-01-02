package com.matewos.z_birr.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.matewos.z_birr.*
import com.matewos.z_birr.databinding.FragmentDashboardBinding
import org.json.JSONObject
import kotlin.properties.Delegates

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var sendRequest: SendRequest
    private lateinit var mQrResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var layout: View
    val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
    val token = sharedPref.getString("Token", "")
    var amount: Double = 0.0
    var firstName = ""
    var lastName = ""


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        sendRequest = ViewModelProvider(this).get(SendRequest::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        layout = root



        mQrResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK){
                val requestcode = 0x0000c0de
                val result = IntentIntegrator.parseActivityResult(requestcode, it.resultCode, it.data)

                if (result.contents != null){
                    //Do something with contents
                    print(result.contents)
                    val resultList = result.contents.split(' ')
                    binding.editTextUserId.setText(resultList[0])
                    try {
                        binding.editTextAmount.setText(resultList[1])
                    }
                    catch (e: IndexOutOfBoundsException){
                        e.printStackTrace()
                    }
                }
            }
        }


        binding.buttonQrScan.setOnClickListener {
            onClickRequestPermission(root)
            val qrScanner = IntentIntegrator(activity)
            qrScanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            qrScanner.setPrompt("Scan receiver's QR Code")
            mQrResultLauncher.launch(qrScanner.createScanIntent())
        }

        binding.buttonPay.setOnClickListener {
            binding.buttonPay.isEnabled = false
            binding.progressBar3.visibility = View.VISIBLE
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, "$BASEURL/user/${binding.editTextUserId.text.toString()}/", null,
                Response.Listener { response ->
                    try {
                        firstName = response.getString("first_name")
                        lastName = response.getString("last_name")
                        binding.buttonPay.isEnabled = true
                        binding.progressBar3.visibility = View.GONE
                        showAlert()
                    }catch (e: java.lang.Exception){
                        Toast.makeText(requireContext(), "User doesn't exist. Try using the QR code scanner", Toast.LENGTH_SHORT).show()
                    }
                    Log.i("Backend", "Response: %s".format(response.toString()))
                },
                Response.ErrorListener { error ->
                    Log.i("Backend", "Response: %s".format(error.toString()))
                    binding.buttonPay.isEnabled = true
                    binding.progressBar3.visibility = View.GONE
                    Toast.makeText(requireContext(), "Make sure you have stable network connection", Toast.LENGTH_SHORT).show()
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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAlert() {
        if (binding.editTextAmount.text.toString() != "" && binding.editTextUserId.text.toString().length == 28) {
            val jsonObject = JSONObject()
            jsonObject.put("uid", binding.editTextUserId.text.toString())
            jsonObject.put("amount", binding.editTextAmount.text.toString())
            Log.i("Backend request", jsonObject.toString())

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Transfer")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
            if (firstName != ""){

                builder.setMessage("Birr " + binding.editTextAmount.text.toString() + " will be transferred to " + firstName + " " + lastName + "\nProceed?")
                builder.setPositiveButton("Yes") { dialog, which ->

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        "$BASEURL/transfer/",
                        jsonObject,
                        Response.Listener { response ->

                            Toast.makeText(requireContext(), response.getString("status"), Toast.LENGTH_SHORT).show()

                            Log.i("Backend", "Response: %s".format(response.toString()))
                        },
                        Response.ErrorListener { error ->
                            Log.i("Backend", "Response: %s".format(error.toString()))
                            Toast.makeText(requireContext(), "Make sure you have stable network connection", Toast.LENGTH_SHORT).show()
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


                    Toast.makeText(
                        requireContext(),
                        "pending...", Toast.LENGTH_SHORT
                    ).show()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "Transfer cancelled", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                builder.setMessage("User doesn't exist.\nTry using the QR code scanner")
                builder.setNeutralButton("OK") { dialog, which ->
                    Toast.makeText(
                        requireContext(),
                        "Transfer error", Toast.LENGTH_SHORT
                    ).show()
                }
            }




            builder.show()
            Log.i("Backend Responsef", SendRequest.respons.toString())

        }
        else{
            Toast.makeText(requireContext(), "Make sure to fill both fields, Amount should be at least Birr 5.00 and user id is 28 characters long", Toast.LENGTH_SHORT).show()
        }
        binding.buttonPay.isEnabled = true
        binding.progressBar3.visibility = View.GONE
    }

    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                activity as Activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_SHORT,
                    null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity as Activity,
                Manifest.permission.CAMERA
            ) -> {

                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_SHORT,
                    getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }
}


