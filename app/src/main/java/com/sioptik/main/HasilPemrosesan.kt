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
import com.sioptik.main.ml.KerasOcrDr
import com.sioptik.main.processing_result.DynamicContentFragment
import com.sioptik.main.processing_result.FullScreenImageActivity
import com.sioptik.main.processing_result.SharedViewModel
import com.sioptik.main.processing_result.json_parser.parser.JsonParser
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HasilPemrosesan : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)

        Log.i("TEST HASIL", intent.getStringExtra("image_uri").toString())

        val imageUriString = intent.getStringExtra("image_uri")

        val model = KerasOcrDr.newInstance(this)
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
                val (processedBitmap, boxes) = processImage(bitmap)
                imageView.setImageBitmap(processedBitmap)

                // Now you can use `boxes` for OCR or any other processing
                // For example, log the box details
                val box = boxes[0]
                val croppedBox = Bitmap.createBitmap(processedBitmap, box.x, box.y, box.width, box.height)
                // Resize the cropped box to match the model's expected input size
                val resizedBox = Bitmap.createScaledBitmap(croppedBox, 200, 31, true)

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 31, 200, 1), DataType.FLOAT32)
                val byteBuffer = convertBitmapToByteBuffer(resizedBox)
                inputFeature0.loadBuffer(byteBuffer)

                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                Log.d("OCR Result", outputFeature0.floatArray[0].toInt().toString())


//                boxes.forEach { box ->
//                    val croppedBox = Bitmap.createBitmap(processedBitmap, box.x, box.y, box.width, box.height)
//                    // Resize the cropped box to match the model's expected input size
//                    val resizedBox = Bitmap.createScaledBitmap(croppedBox, 200, 31, true)
//
//                    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 31, 200, 1), DataType.FLOAT32)
//                    val byteBuffer = convertBitmapToByteBuffer(resizedBox)
//                    inputFeature0.loadBuffer(byteBuffer)
//
//                    val outputs = model.process(inputFeature0)
//                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//                    Log.d("Detected Box", "Box at (${box.x}, ${box.y}) with width ${box.width} and height ${box.height}")
//                }

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

private fun processImage(bitmap: Bitmap): Pair<Bitmap, List<Rect>> {
    val imgProcessor = ImageProcessor()

    // Initial Mat conversion
    val originalMat = imgProcessor.convertBitmapToMat(bitmap)
    val processedMat = imgProcessor.preprocessImage(originalMat)

    // Detect Boxes
    val boxes = imgProcessor.detectBoxes(processedMat)

    // Visualize contours and rectangles on the processed image
    val resultImage = imgProcessor.visualizeContoursAndRectangles(processedMat, boxes, Scalar(255.0, 0.0, 0.0), true, 2)

    // Convert the final processed Mat back to a Bitmap
    val resultBitmap = imgProcessor.convertMatToBitmap(resultImage)

    // Return the processed bitmap along with the list of detected boxes
    return Pair(resultBitmap, boxes)
}
fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val byteBuffer = ByteBuffer.allocateDirect(4 * bitmap.width * bitmap.height)
    byteBuffer.order(ByteOrder.nativeOrder())
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (pixel in pixels) {
        val r = (pixel shr 16 and 0xFF)
        val g = (pixel shr 8 and 0xFF)
        val b = (pixel and 0xFF)
        // Assuming model expects grayscale input
        val normalizedPixelValue = (0.2989 * r + 0.5870 * g + 0.1140 * b) / 255.0
        byteBuffer.putFloat(normalizedPixelValue.toFloat())
    }
    return byteBuffer
}

