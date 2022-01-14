package com.matewos.z_birr.ui.notifications

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.matewos.z_birr.*
import com.matewos.z_birr.database.AppDatabase
import com.matewos.z_birr.database.Transaction
import com.matewos.z_birr.database.TransactionDao
import com.matewos.z_birr.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    val sharedPref = SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
    val token = sharedPref.getString("Token", "")
    lateinit var db: AppDatabase
    lateinit var transactionDao: TransactionDao
    lateinit var recyclerView: RecyclerView
    lateinit var transactionAdapter: TransactionAdapter
    lateinit var transactions: MutableList<Transaction>
    lateinit var tempTransactions: MutableList<Transaction>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var scope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transactions_menu, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val inputMethodManager =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val searchText = p0!!.lowercase()

                if (searchText.isNotEmpty()) {
                    transactions.clear()
                    transactions.addAll(tempTransactions.filter {
                        it.fullName!!.contains(
                            searchText,
                            ignoreCase = true
                        ) || it.userId!!.contains(searchText, ignoreCase = true)
                    })
                    transactionAdapter.notifyDataSetChanged()
                } else {
                    transactions.clear()
                    transactions.addAll(tempTransactions)
                    transactionAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                item.isEnabled = false
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET, "$BASEURL/transactiontable/${transactionDao.count()}/", null,
                    Response.Listener { response ->
                        if (response.getString("transactions") == "up to date") {
                            Toast.makeText(context, "Up to date", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val jsonArray = response.getJSONArray("transactions")
                            var calendar = Calendar.getInstance()
                            var temp: JSONObject
                            var value: String
                            scope.launch{
                            for (i in 0 until jsonArray.length()) {
                                temp = jsonArray[i] as JSONObject
                                value = temp.getString("date")
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                val date = sdf.parse(value)// all done
                                calendar = sdf.getCalendar()

                                Log.i("Backend", jsonArray[i].toString())

                                transactionDao.insert(
                                    temp.getString("fullName"),
                                    temp.getString("uid"),
                                    temp.getDouble("balance"),
                                    temp.getDouble("amount"),
                                    temp.getBoolean("sender"),
                                    calendar
                                )


                                calendar = Calendar.getInstance()
                            }

                            transactions.clear()
                            transactions.addAll(transactionDao.getAllByName())
                            tempTransactions.clear()
                            tempTransactions.addAll(transactions)
                            transactionAdapter.notifyDataSetChanged()
                            recyclerView.smoothScrollToPosition(0)
                            Log.i("Backend----", transactionDao.getAll().toString())

                            }
                            if (context != null)
                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                        }

                        //binding.transactionsList.adapter = adapter
                        item.isEnabled = true

                    },
                    Response.ErrorListener { error ->
                        item.isEnabled = true
                        Log.i("Backend", "Response: %s".format(error.toString()))
                        if(isAdded) {
                            Toast.makeText(
                                activity,
                                "Make sure you are connected to a stable connection and try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

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
    ): View {
        scope = CoroutineScope(Dispatchers.Main)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = AppDatabase.getDatabase(requireContext())
        transactionDao = db.transactionDao()

        scope.launch { initView(root) }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun initView(view: View) {
        recyclerView = view.findViewById(R.id.transactionsRecyclerView)
        transactions = transactionDao.getAllByName().toMutableList()
        tempTransactions = mutableListOf()
        tempTransactions.addAll(transactions)
        transactionAdapter = TransactionAdapter(transactions) {
            if (it != null) {
                val bundle = Bundle()
                bundle.putString("uid", it.userId)
                findNavController().navigate(
                    R.id.action_navigation_notifications_to_detailsFragment,
                    bundle
                )
            }
        }
        recyclerView.adapter = transactionAdapter
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

}