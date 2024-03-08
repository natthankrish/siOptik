package com.example.sioptik

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HasilPemrosesan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hasil_pemrosesan)

        val frameLayout = findViewById<FrameLayout>(R.id.imagePlaceholderLeft)

        val imageUriString = intent.getStringExtra("image_uri")
        val imageUri = Uri.parse(imageUriString)

        val imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageURI(imageUri)
        }

        frameLayout.addView(imageView)
    }
}
