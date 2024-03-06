package com.example.sioptik

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class HasilFoto :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hasil_foto)

        val imageUri: Uri = Uri.parse(intent.getStringExtra("image_uri"))
        val imageView: ImageView = findViewById(R.id.image_preview)
        imageView.setImageURI(imageUri)
        val downloadButton = findViewById<Button>(R.id.download_button)
        downloadButton.setOnClickListener {
            downloadImage(imageUri) // Call downloadImage here with imageUri
        }
    }
    private fun downloadImage(imageUri: Uri) {
        val inputStream = contentResolver.openInputStream(imageUri) ?: return
        val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"
        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(picturesDirectory, fileName)
        val outputStream = FileOutputStream(imageFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        // Notify gallery about the new image
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = Uri.fromFile(imageFile)
            sendBroadcast(mediaScanIntent)
        }

        Toast.makeText(this, "Image downloaded to Pictures", Toast.LENGTH_SHORT).show()
    }

}