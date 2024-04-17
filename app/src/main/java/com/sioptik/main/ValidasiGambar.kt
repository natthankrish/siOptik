package com.sioptik.main

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sioptik.main.apriltag.AprilTagDetection
import com.sioptik.main.apriltag.AprilTagNative
import com.sioptik.main.image_processor.ImageProcessor
import com.sioptik.main.processing_result.FullScreenImageActivity
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import java.util.ArrayList

class ValidasiGambar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.validasi_gambar)

        val frameLayout = findViewById<FrameLayout>(R.id.imageValidationContainer)
        val apriltagTagView = findViewById<Button>(R.id.april_tag)
        val imageUriString = intent.getStringExtra("image_uri")
        AprilTagNative.apriltag_init("tag36h10", 2, 1.0, 0.0, 4)


        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                val processedBitmap = processImage(bitmap)
                Log.i("DIMENSION", "SEBELUM CONVERT TO NV21");

                val width = processedBitmap.width
                val height = processedBitmap.height
                val byteArray = getNV21(width, height/4, processedBitmap)
                Log.i("DIMENSION", width.toString() + " " + height.toString());
                val detections : ArrayList<AprilTagDetection> = AprilTagNative.apriltag_detect_yuv(byteArray, width, height)
                Log.i("DIMENSION", "SETELAH APRILTAG");
                if (detections.size == 0) {
                    throw Exception()
                }

                val apriltag = detections[0]
                apriltagTagView.text = apriltag.id.toString()

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
        val borderImageDrawable = R.drawable.border_smaller
        val borderImageSmallDrawable = R.drawable.border_smallest
        val borderImage = imgProcessor.loadDrawableImage(this, borderImageDrawable)
        val borderImageSmall = imgProcessor.loadDrawableImage(this, borderImageSmallDrawable)

        // Initial Mat
        val originalMat = imgProcessor.convertBitmapToMat(bitmap)
        val processedMat = imgProcessor.preprocessImage(originalMat)

        // SplittedImages
        val splittedImagesRects = imgProcessor.splitImageRects(originalMat)
        val splittedImages = imgProcessor.splitImage(originalMat)
        // Log.i("TESTIMG", splittedImages.toString())
        val borderContainer = mutableListOf<Rect>()

        splittedImages.forEachIndexed { index, mat ->
            // Detect Borders
            var border = imgProcessor.detectBorder(imgProcessor.convertToGray(mat), imgProcessor.convertToGray(borderImage))
            if (border == null){
                border = imgProcessor.detectBorder(imgProcessor.convertToGray(mat), imgProcessor.convertToGray(borderImageSmall))
            }
            // Log.i("TEST BORDER", border.toString())

            if (border != null){
                // Assume that it will only get 1
                val currentRect = splittedImagesRects[index]
                val adjustedBorder = Rect((currentRect.x + border.x), (currentRect.y + border.y), border.width, border.height)
                borderContainer.add(adjustedBorder)
            } else {
                Log.i("TEST BORDER DETECTION", "Border Not Found")
            }
        }
        val borderedMat =imgProcessor.visualizeContoursAndRectangles(originalMat, borderContainer, "B")

        // Detect Boxes
        val boxes = imgProcessor.detectBoxes(processedMat)
        val resultImage = imgProcessor.visualizeContoursAndRectangles(borderedMat, boxes, "R")
        // Log.i("TEST", boxes.toString())

        // Crop
        var croppedResultImage = resultImage
        if (borderContainer.size == 4){
            val padding = 30;
            val w = originalMat.width()
            val h = originalMat.height()
//            Log.i("TEST W H", w.toString() + "||" +h.toString())
            val tl_rect = borderContainer[0]
            val br_rect = borderContainer[3]
//            Log.i("TEST TL BR", tl_rect.toString() + "||" + br_rect.toString())
            var tlx = if (tl_rect.x - padding <= 0) 0 else tl_rect.x - padding
            val tly = if (tl_rect.y - padding <= 0) 0 else tl_rect.y - padding
            val brx = if (br_rect.x + (br_rect.width) + padding >= w) w else br_rect.x + (br_rect.width) + padding
            val bry = if (br_rect.y + (br_rect.height) + padding >= h) h else br_rect.y + (br_rect.height) + padding
//            Log.i("TEST BOUNDARIES", tlx.toString() + "||" + tly.toString() + "||" + brx.toString() + "||" + bry.toString())

            croppedResultImage = Mat(resultImage, Rect(tlx, tly, (brx - tlx), (bry - tly)))
        }
        return imgProcessor.convertMatToBitmap(croppedResultImage)
    }

    fun getNV21(inputWidth: Int, inputHeight: Int, scaled: Bitmap): ByteArray? {
        var scaled = scaled
        val argb = IntArray(inputWidth * inputHeight)

        // Ensure that the bitmap is in ARGB_8888 format
        scaled = scaled.copy(Bitmap.Config.ARGB_8888, true)
        scaled.getPixels(argb, 0, inputWidth, 3 * (inputWidth / 4) - 1, 0, inputWidth / 4, inputHeight)
        val yuv = ByteArray(inputWidth * inputHeight * 3 / 2)
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight)
        scaled.recycle()
        return yuv
    }

    fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        try {
            val frameSize = width * height
            var yIndex = 0
            var uvIndex = frameSize
            var a: Int
            var R: Int
            var G: Int
            var B: Int
            var Y: Int
            var U: Int
            var V: Int
            var index = 0
            for (j in 0 until height) {
                for (i in 0 until width) {
                    a = argb[index] and -0x1000000 shr 24 // a is not used obviously
                    R = argb[index] and 0xff0000 shr 16
                    G = argb[index] and 0xff00 shr 8
                    B = argb[index] and 0xff shr 0

                    // well known RGB to YUV algorithm
                    Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
                    U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
                    V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128

                    // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                    //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                    //    pixel AND every other scanline.
                    yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
                    if (j % 2 == 0 && index % 2 == 0) {
                        yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                        yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
                    }
                    index++
                }
            }
        } catch (e: IndexOutOfBoundsException) {
        }
    }

}