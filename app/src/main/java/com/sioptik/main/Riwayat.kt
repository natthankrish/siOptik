package com.sioptik.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sioptik.main.riwayat_repository.RiwayatEntity
import java.util.Date

class Riwayat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        val dataset = listOf(RiwayatEntity(1, 101, Date(), "test", "test", "test"))
        val customAdapter = RiwayatRecyclerViewAdapter(dataset)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = customAdapter
    }
}