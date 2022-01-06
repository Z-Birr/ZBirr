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

class TransactionAdapter(private val dataSet: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullName: TextView
        val userId: TextView
        val amount: TextView
        val date: TextView
        val image: ImageView
        init {
            // Define click listener for the ViewHolder's View.
            fullName = view.findViewById(R.id.transactionFullName)
            userId = view.findViewById(R.id.transactionUserId)
            amount = view.findViewById(R.id.textViewAmountTransfered)
            date = view.findViewById(R.id.textViewDate)
            image = view.findViewById(R.id.transactionImage)
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
        viewHolder.userId.text = dataSet[position].userId
        viewHolder.amount.text = String.format("%.${2}f",dataSet[position].amount)
        viewHolder.fullName.text = dataSet[position].fullName
        val calendar: Calendar = dataSet[position].date!!.clone() as Calendar
        val simpleDateFormat = SimpleDateFormat("E, dd MMM yyyy HH:mm")
        viewHolder.date.text = simpleDateFormat.format(calendar.time)
        if (dataSet[position].sender!!){
            viewHolder.image.setImageResource(R.drawable.ic_baseline_send_24)
        }
        else {
            viewHolder.image.setImageResource(R.drawable.ic_baseline_receive_24)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}