package com.sioptik.main.processing_result

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.sioptik.main.R

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.fullScreenImageView)
        val imageUriString = intent.getStringExtra("imageUri")

        if (imageUriString != null) {
            imageView.setImageURI(Uri.parse(imageUriString))
        } else {
            finish()
        }

        val imageCloseButton: ImageButton = findViewById(R.id.closeButton)
        imageCloseButton.setOnClickListener {
            finish()
        }
    }
}