package com.matewos.z_birr.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matewos.z_birr.R
import com.matewos.z_birr.database.Transaction
import java.text.SimpleDateFormat
import java.util.*
import com.matewos.z_birr.databinding.TransactionRowItemBinding


class TransactionAdapter(
    private val dataSet: MutableList<Transaction>,
    private val onSelect: (Transaction?) -> Unit
) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    lateinit var recyclerView: RecyclerView

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vieww = view
        val fullName: TextView
        val amount: TextView
        val date: TextView
        val image: ImageView
        var transactionPosition = 0

        init {
            // Define click listener for the ViewHolder's View.
            fullName = view.findViewById(R.id.transactionFullName)
            amount = view.findViewById(R.id.textViewAmountTransfered)
            date = view.findViewById(R.id.textViewDate)
            image = view.findViewById(R.id.transactionImage)
        }

        fun bind(transaction: Transaction, onSelect: (Transaction?) -> Unit) {
            val item = TransactionRowItemBinding.bind(itemView)
            item.cardView.setOnClickListener {
                onSelect(transaction)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.transaction_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.amount.text = String.format("%.${2}f", dataSet[position].amount)
        viewHolder.fullName.text = dataSet[position].fullName
        val calendar: Calendar = dataSet[position].date!!
        val simpleDateFormat = SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.US)
        viewHolder.date.text = simpleDateFormat.format(calendar.time)
        if (dataSet[position].sender!!) {
            viewHolder.image.setImageResource(R.drawable.ic_baseline_send_24)
        } else {
            viewHolder.image.setImageResource(R.drawable.ic_baseline_receive_24)
        }
        viewHolder.transactionPosition = position
        viewHolder.bind(dataSet[position], onSelect)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}