package com.sioptik.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ValidasiGambar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.validasi_gambar)

        val frameLayout = findViewById<FrameLayout>(R.id.imageValidationContainer)

        val imageUriString = intent.getStringExtra("image_uri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            try {
                val imageView = ImageView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageURI(imageUri)
                }
                frameLayout.addView(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception as needed
                Toast.makeText(this, "Image Processing Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle case where imageUriString is null
            Toast.makeText(this, "Image is NULL", Toast.LENGTH_SHORT).show()
        }


        val retryButton = findViewById<Button>(R.id.retryButton)
        val sendButton = findViewById<Button>(R.id.sendButton)

        retryButton.setOnClickListener {
            val cameraIntent = Intent(this, Kamera::class.java)
            startActivity(cameraIntent)
        }

        sendButton.setOnClickListener {
            Intent(this, HasilPemrosesan::class.java).also { previewIntent ->
                previewIntent.putExtra("image_uri", imageUriString)
                startActivity(previewIntent)
            }
        }
    }
}
