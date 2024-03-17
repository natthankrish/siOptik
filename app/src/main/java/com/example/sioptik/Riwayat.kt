package com.example.sioptik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sioptik.databinding.KameraBinding

class Riwayat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_riwayat)

        val dataset = arrayOf("January", "February", "March", "January", "February", "March", "January", "February", "March", "January", "February", "March", "January", "February", "March", "January", "February", "March", "January", "February", "March", "January", "February", "March")
        val customAdapter = RecyclerViewAdapter(dataset)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}