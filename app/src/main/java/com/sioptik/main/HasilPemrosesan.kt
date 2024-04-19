package com.sioptik.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.sioptik.main.processing_result.DynamicContentFragment
import com.sioptik.main.processing_result.FullScreenImageActivity
import com.sioptik.main.processing_result.SharedViewModel
import com.sioptik.main.processing_result.json_parser.parser.JsonParser

class HasilPemrosesan : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)

        Log.i("TEST HASIL", intent.getStringExtra("image_uri").toString())

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)

        imageView.setImageURI(imageUri)
        imageView.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("imageUri", imageUriString)
            startActivity(intent)
        }

        val jsonString = """
            {
              "title": "Hasil Pemilihan Presiden RI",
              "description": "Deskripsi Hihihi",
              "aprilTagId": 100,
              "tpsId": 10,
              "candidates": [
                {
                  "orderNumber": 1,
                  "choiceName": "Alis",
                  "totalVoters": 500000
                },
                {
                  "orderNumber": 2,
                  "choiceName": "Prabski",
                  "totalVoters": 450000
                },
                {
                  "orderNumber": 3,
                  "choiceName": "Skipper",
                  "totalVoters": 350000
                }
              ]
            }
            """.trimIndent()

        val jsonData = JsonParser.parse(jsonString)
        viewModel.jsonData = jsonData

        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, DynamicContentFragment())
        }
    }
}