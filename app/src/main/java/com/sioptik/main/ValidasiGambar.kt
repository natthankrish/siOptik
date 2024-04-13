package com.sioptik.main

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sioptik.main.image_processor.ImageProcessor


class ValidasiGambar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.validasi_gambar)

        val frameLayout = findViewById<FrameLayout>(R.id.imageValidationContainer)
        val imageUriString = intent.getStringExtra("image_uri")

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                val processedBitmap = processImage(bitmap)

                val imageView = ImageView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageBitmap(processedBitmap)
                }
                frameLayout.addView(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load or process image", Toast.LENGTH_SHORT).show()
            }
        } else {
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

    private fun processImage(bitmap: Bitmap): Bitmap {
        val imgProcessor = ImageProcessor()

        val originalMat = imgProcessor.convertBitmapToMat(bitmap)
        val processedMat = imgProcessor.preprocessImage(originalMat)
        val rectangles = imgProcessor.detectRectangles(processedMat)

        return imgProcessor.visualizeContoursAndRectangles(processedMat, rectangles)
    }

}
