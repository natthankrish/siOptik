package com.sioptik.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.sioptik.main.riwayat_repository.RiwayatEntity

class RiwayatRecyclerViewAdapter(private val riwayatLiveData: List<RiwayatEntity>) :
    RecyclerView.Adapter<RiwayatRecyclerViewAdapter.ModelViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RiwayatRecyclerViewAdapter.ModelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.riwayat_card, parent, false)
        return ModelViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: RiwayatRecyclerViewAdapter.ModelViewHolder, position: Int) {
        val riwayat = riwayatLiveData[position]
        holder.bind(riwayat)
    }

    override fun getItemCount(): Int {
        return riwayatLiveData.count()
    }

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView
        var dateTextView: TextView
        var apriltagIdTextView: TextView
        var timeTextView: TextView

        init {
            cardView = itemView.findViewById(R.id.riwayat_card)
            dateTextView = itemView.findViewById(R.id.date_textview)
            apriltagIdTextView = itemView.findViewById(R.id.apriltag_id_textview)
            timeTextView = itemView.findViewById(R.id.time_textview)
        }

        fun bind(riwayat: RiwayatEntity) {
            val date = riwayat.date
            val dateText = "${date.day}/${date.month}/${date.year}"
            val timeText = "${date.hours}:${date.minutes}"
            dateTextView.text = dateText
            apriltagIdTextView.text = riwayat.apriltagId.toString()
            timeTextView.text = timeText
        }
    }
}
