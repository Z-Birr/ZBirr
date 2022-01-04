package com.matewos.z_birr.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.matewos.z_birr.*
import com.matewos.z_birr.databinding.AlertdialogPasswordBinding
import com.matewos.z_birr.databinding.FragmentHomeBinding
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hello : TextView = binding.hello
        val balance: TextView = binding.balance
        val sharedPref = SplashScreen.instance.getSharedPreferences(STATE, Context.MODE_PRIVATE)

        balance.setText("Current Balance: ETB ")
        homeViewModel.firstName.observe(viewLifecycleOwner, {
            hello.text = "Hello, $it "
        })
        homeViewModel.lastName.observe(viewLifecycleOwner, {
            hello.text = "${hello.text} $it"
        })

        auth = Firebase.auth

        binding.balance.setText(sharedPref.getString("currentBalance", ""))
        binding.floatingActionButtonRefresh.setOnClickListener{
            it.visibility = View.GONE
            binding.progressBar4.visibility = View.VISIBLE
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                "$BASEURL/currentbalance/",
                null,
                Response.Listener { response ->
                    try {
                        val balance = response.getString("currentBalance")
                        binding.balance.setText(balance.toString())
                        with(sharedPref?.edit()) {
                            this?.putString("currentBalance", balance)
                            this?.apply()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(requireContext(), "Something went wrong try again later", Toast.LENGTH_SHORT).show()
                    }
                    it.visibility = View.VISIBLE
                    binding.progressBar4.visibility = View.GONE
                },
                Response.ErrorListener { error ->
                    Toast.makeText(requireContext(), "Make sure you are connected to a stable connection and try again", Toast.LENGTH_SHORT).show()
                    it.visibility = View.VISIBLE
                    binding.progressBar4.visibility = View.GONE
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    val token = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE).getString("Token", "")

                    params["Authorization"] = "Token $token"
                    //..add other headers
                    return params
                }
            }
            MySingleton.getInstance(SplashScreen.instance.applicationContext).addToRequestQueue((jsonObjectRequest))

        }

        binding.buttonGenerateQrCode.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Scan QR code to get payment")
            val qrView = layoutInflater.inflate(R.layout.alertdialog_qr_generator, null)

            builder.setView(qrView)
            builder.show()



            val amount = binding.editTextAmountRequest.text.toString()
            val data = auth.currentUser!!.uid.toString() + " " + amount

            if (data.isEmpty()) {
                Toast.makeText(activity, "Error: Please restart the app", Toast.LENGTH_SHORT).show()
            } else {
                val writer = QRCodeWriter()
                try {
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    qrView.findViewById<ImageView>(R.id.imageUserIdQr2).setImageBitmap(bmp)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}