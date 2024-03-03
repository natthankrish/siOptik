package com.example.sioptik

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HasilFoto :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hasil_foto)

        val imageUri: Uri = Uri.parse(intent.getStringExtra("image_uri"))
        val imageView: ImageView = findViewById(R.id.image_preview)
        imageView.setImageURI(imageUri)
    }
}