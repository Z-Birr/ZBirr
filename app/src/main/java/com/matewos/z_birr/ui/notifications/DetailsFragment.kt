package com.matewos.z_birr.ui.notifications

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.content.ClipboardManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matewos.z_birr.R
import com.matewos.z_birr.database.AppDatabase
import com.matewos.z_birr.database.Transaction
import com.matewos.z_birr.database.TransactionDao
import com.matewos.z_birr.databinding.FragmentDetailsBinding
import com.matewos.z_birr.databinding.FragmentNotificationsBinding

class DetailsFragment : Fragment() {
    lateinit var binding: FragmentDetailsBinding
    lateinit var db: AppDatabase
    lateinit var transactionDao: TransactionDao
    lateinit var transactions: MutableList<Transaction>
    lateinit var recyclerView: RecyclerView
    lateinit var transactionDetailAdapter: TransactionDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.textViewUserId.setOnClickListener {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip : ClipData = ClipData.newPlainText("userId", binding.textViewUserId.text.toString())
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "user id copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        db = AppDatabase.getDatabase(requireContext())
        transactionDao = db.transactionDao()
        transactions = arguments?.getString("uid", "")?.let { transactionDao.search("%"+it+"%").toMutableList() }!!

        binding.name.setText(transactions[0].fullName)
        binding.textViewUserId.setText(transactions[0].userId)

        recyclerView = binding.recyclerView
        transactionDetailAdapter = TransactionDetailAdapter(transactions)
        recyclerView.adapter = transactionDetailAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                Log.i("Search", "alksdlflka")
                requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}