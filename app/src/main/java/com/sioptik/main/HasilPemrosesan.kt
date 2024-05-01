package com.sioptik.main

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.sioptik.main.image_processor.ImageProcessor
import com.sioptik.main.ml.KerasOcrDr
import com.sioptik.main.ml.KerasOcrFloat16
import com.sioptik.main.ml.Model
import com.sioptik.main.ml.Ocr
import com.sioptik.main.processing_result.DynamicContentFragment
import com.sioptik.main.processing_result.FullScreenImageActivity
import com.sioptik.main.processing_result.SharedViewModel
import com.sioptik.main.processing_result.json_parser.parser.JsonParser
import org.opencv.core.Mat
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
        val imageUriString = intent.getStringExtra("image_uri")
        val button : Button = findViewById(R.id.retry_processing_button)

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
                val model = Ocr.newInstance(this)

                val croppedBoxes = cropBoxes(bitmap, detectedBoxes)
//                Log.i("TEST CROPPED BOXES", croppedBoxes.toString())
                val box = croppedBoxes[0]
                val resizedBox = Bitmap.createScaledBitmap(box, 35,35, true)
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 35, 35), DataType.FLOAT32)
                val byteBuffer = convertBitmapToByteBuffer(resizedBox)

                inputFeature0.loadBuffer(byteBuffer)

                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val result = outputFeature0.floatArray
                Log.i("OCR :", result.size.toString())
                result.forEach {
                    Log.i("Result : ", it.toString())
                }
                val alphabets = "0123456789abcdefghijklmnopqrstuvwxyz"  // example alphabet


                val maxIndex = result.indices.maxBy { result[it] } ?: -1
                val resultString = "Prediction Result: %d\nConfidence: %2f".
                format(maxIndex, result[maxIndex])

                Log.i("OCR :" , resultString)
                model.close()
                imageView.setImageBitmap(resizedBox)
                button.setOnClickListener {
                    saveImageToGallery(this, box, "CroppedImage", "Cropped image saved from OCR processing")

                }
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

private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

    val byteBuffer = ByteBuffer.allocateDirect(4 * bitmap.width * bitmap.height)
    byteBuffer.order(ByteOrder.nativeOrder())
    byteBuffer.rewind()

    val intValues = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    for (pixelValue in intValues) {
        val red = ((pixelValue shr 16) and 0xFF)
        val green = ((pixelValue shr 8) and 0xFF)
        val blue = (pixelValue and 0xFF)
        val normalizedPixelValue = (red + green + blue) / 3.0f
        byteBuffer.putFloat(normalizedPixelValue)
    }
    return byteBuffer
}

fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String, description: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, title)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourAppName")
    }

    val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        // Memastikan outputStream tidak null sebelum penggunaan
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            // Compressing the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } ?: run {
            Toast.makeText(context, "Failed to open output stream", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_LONG).show()
    } ?: run {
        Toast.makeText(context, "Failed to Save", Toast.LENGTH_LONG).show()
    }
}

