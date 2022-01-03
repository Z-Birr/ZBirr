package com.matewos.z_birr.ui.notifications

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.matewos.z_birr.*
import com.matewos.z_birr.databinding.FragmentNotificationsBinding
import org.json.JSONArray
import org.json.JSONObject

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
    val token = sharedPref.getString("Token", "")
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, "$BASEURL/transactiontable/", null,
            Response.Listener { response ->
                val jsonArray = response.getJSONArray("transactions")
                val transactions = arrayOfNulls<String>(jsonArray.length())
                for (i in 0 until jsonArray.length()){
                    transactions[i] = jsonArray[i].toString()
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, transactions)
                binding.transactionsList.adapter = adapter
                Log.i("Backend", "Response: %s".format(response.toString()))
            },
            Response.ErrorListener { error ->
                Log.i("Backend", "Response: %s".format(error.toString()))

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




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}