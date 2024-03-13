package com.example.sioptik

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.sioptik.databinding.FragmentHalamanUtamaBinding

class HalamanUtama : Fragment() {
    private var _binding : FragmentHalamanUtamaBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHalamanUtamaBinding.inflate(inflater, container, false)
        binding.scanButton.setOnClickListener {
            val cameraIntent = Intent(activity, Kamera::class.java)
            startActivity(cameraIntent)
        }

        binding.imageButton.setOnClickListener {
            val imageIntent = Intent(activity, Gambar::class.java)
            startActivity(imageIntent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}