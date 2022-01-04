package com.matewos.z_birr.ui.notifications

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transactions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                item.isEnabled=false
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, "$BASEURL/transactiontable/0/", null,
                    Response.Listener { response ->
                        val jsonArray = response.getJSONArray("transactions")
                        val transactions = arrayOfNulls<String>(jsonArray.length())
                        var temp  = ""
                        for (i in 0 until jsonArray.length()) {
                            temp = jsonArray[i].toString()
                            Log.i("Backend", temp)
                            transactions[i] = temp
                        }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            transactions
                        )
                        binding.transactionsList.adapter = adapter
                        item.isEnabled = true
                        Log.i("Backend", "Response: %s".format(response.toString()))
                    },
                    Response.ErrorListener { error ->
                        item.isEnabled = true
                        Log.i("Backend", "Response: %s".format(error.toString()))
                        Toast.makeText(requireContext(), "Make sure you are connected to a stable connection and try again", Toast.LENGTH_SHORT).show()

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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}