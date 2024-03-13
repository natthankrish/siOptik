package com.example.sioptik

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sioptik.databinding.GambarBinding
import org.opencv.android.OpenCVLoader

class Gambar : AppCompatActivity(){
    private lateinit var viewBinding: GambarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = GambarBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (OpenCVLoader.initDebug()){
            Log.d ("LOADED", "success")
        }
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gambar, container, false)
//    }
}