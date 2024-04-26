package com.sioptik.main

import com.sioptik.main.riwayat_repository.RiwayatEntity

interface RiwayatInteractionListener {
    fun onDeleteRiwayat(riwayat: RiwayatEntity)
}
