package com.sioptik.main

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.sioptik.main.image_processor.ImageProcessor
import com.sioptik.main.processing_result.DynamicContentFragment
import com.sioptik.main.processing_result.FullScreenImageActivity
import com.sioptik.main.processing_result.SharedViewModel
import com.sioptik.main.processing_result.json_parser.parser.JsonParser
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar

class HasilPemrosesan : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)
        val imageUriString = intent.getStringExtra("image_uri")


        if (imageUriString != null){
            val imageUri = Uri.parse(imageUriString)

            imageView.setImageURI(imageUri)
            imageView.setOnClickListener {
                val intent = Intent(this, FullScreenImageActivity::class.java)
                intent.putExtra("imageUri", imageUriString)
                startActivity(intent)
            }

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                // Process Image
                val detectedBoxes = detectBoxes(bitmap)
                val processedBitmap = processImage(bitmap, detectedBoxes)

                // Crop Detected Boxes for OCR
                val croppedBoxes = cropBoxes(bitmap, detectedBoxes)
//                Log.i("TEST CROPPED BOXES", croppedBoxes.toString())

                imageView.setImageBitmap(processedBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load or process image", Toast.LENGTH_SHORT).show()
            }

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

private fun processImage (bitmap: Bitmap, boxes: List<Rect>) : Bitmap {
    val imgProcessor = ImageProcessor()
    // Initial Mat
    val originalMat = imgProcessor.convertBitmapToMat(bitmap)
    val processedMat = imgProcessor.preprocessImage(originalMat) // Maybe will be used but better save it first
    // Contour Boxes
    val resultImage = imgProcessor.visualizeContoursAndRectangles(processedMat, boxes, Scalar(255.0, .0, 0.0), true, 2)
    return imgProcessor.convertMatToBitmap(resultImage)
}

private fun cropBoxes(bitmap: Bitmap, boxes: List<Rect>) : List<Bitmap> {
    val imgProcessor = ImageProcessor()
    // Initial Mat
    val originalMat = imgProcessor.convertBitmapToMat(bitmap)
    val croppedImages = mutableListOf<Bitmap>()
    boxes.forEach{box ->
        val newMat : Mat = Mat(originalMat, box)
        val newBitmap = imgProcessor.convertMatToBitmap(newMat)
        croppedImages.add(newBitmap)
    }
    return croppedImages
}


private fun detectBoxes (bitmap: Bitmap) : List<Rect> {
    val imgProcessor = ImageProcessor()
    // Initial Mat
    val originalMat = imgProcessor.convertBitmapToMat(bitmap)
    val processedMat = imgProcessor.preprocessImage(originalMat)
    // Detect Boxes
    val boxes = imgProcessor.detectBoxes(processedMat)
    return boxes
}

