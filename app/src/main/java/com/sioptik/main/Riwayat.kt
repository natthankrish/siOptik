package com.sioptik.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sioptik.main.riwayat_repository.RiwayatEntity
import com.sioptik.main.riwayat_repository.RiwayatViewModel
import com.sioptik.main.riwayat_repository.RiwayatViewModelFactory

class Riwayat : AppCompatActivity(), RiwayatInteractionListener {
    private val riwayatViewModel: RiwayatViewModel by viewModels {
        RiwayatViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        val customAdapter = RiwayatRecyclerViewAdapter(emptyList(), this)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = customAdapter

        riwayatViewModel.getAllRiwayat().observe(this) {
            customAdapter.updateData(it)
        }
    }

    override fun onDeleteRiwayat(riwayat: RiwayatEntity) {
        riwayatViewModel.deleteRiwayat(riwayat)
    }
}