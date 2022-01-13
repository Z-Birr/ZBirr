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

class TransactionDetailAdapter (private val dataSet: MutableList<Transaction>) :
RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder>() {
        lateinit var recyclerView: RecyclerView

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val amount: TextView
            val date: TextView
            val image: ImageView
            init {
                // Define click listener for the ViewHolder's View.
                amount = view.findViewById(R.id.textViewAmount)
                date = view.findViewById(R.id.textViewdate)
                image = view.findViewById(R.id.imageView)
            }

        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.detail_row_item, viewGroup, false)

            return ViewHolder(view)
        }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.amount.text = String.format("%.${2}f",dataSet[position].amount)
            val calendar: Calendar = dataSet[position].date!!.clone() as Calendar
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
            val simpleDateFormat = SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.US)
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