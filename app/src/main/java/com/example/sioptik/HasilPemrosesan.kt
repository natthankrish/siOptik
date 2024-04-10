package com.example.sioptik

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.sioptik.processing_result.DynamicContentFragment
import com.example.sioptik.processing_result.FullScreenImageActivity
import com.example.sioptik.processing_result.SharedViewModel
import com.example.sioptik.processing_result.json_parser.parser.JsonParser

class HasilPemrosesan : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)

        imageView.setImageURI(imageUri)
        imageView.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("imageUri", imageUriString)
            startActivity(intent)
        }

        val jsonString = "{\"title\":\"Example Title\",\"description\":\"This is a description.\"}"

        val jsonData = JsonParser.parse(jsonString)
        viewModel.jsonData = jsonData

        supportFragmentManager.commit {
            replace(R.id.fragmentContainerView, DynamicContentFragment())
        }
    }
}