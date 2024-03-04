package com.example.sioptik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            val intent = Intent(activity, Kamera::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.scanButton.setOnClickListener {
//            val action = HalamanUtamaDirections.actionHalamanUtamaToKamera()
//            val navController = findNavController()
//            navController.navigate(action)
//        }

    }

}