package com.example.sioptik

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sioptik.databinding.FragmentHalamanUtamaBinding
import com.example.sioptik.databinding.FragmentRiwayatBinding

class RecyclerViewAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val item: Button

        init {
            item = view.findViewById(R.id.item_riwayat)
            item.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = dataSet[position]
                    val intent = Intent(itemView.context, DetailRiwayat::class.java)
                    intent.putExtra("data", clickedItem)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_riwayat, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.item.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

}
