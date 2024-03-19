package com.example.sioptik

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.sioptik.processing_result.FullScreenImageActivity
import com.example.sioptik.processing_result.fragment_result_april_tag_types.AprilTagType000Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class HasilPemrosesan : AppCompatActivity() {

    private lateinit var apriltagNative: ApriltagNative

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)
        val textView : TextView = findViewById(R.id.april_tag)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)

        imageView.setImageURI(imageUri)

        imageView.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java).apply {
                putExtra("imageUri", imageUriString)
            }
            startActivity(intent)
        }

        apriltagNative = ApriltagNative().apply {
            native_init()
        }

        val detection = detectAprilTag(imageUri)
        Log.d("AprilTagDetection", "Detection result: ${detection?.id}") // Debug log
    }

    private fun detectAprilTag(imageUri : Uri) : ApriltagDetection?{
        val source = ImageDecoder.createSource(this.contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        Log.d("AprilTagDetection",bitmap.toString())

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        Log.d("AprilTagDetection",image.isEmpty().toString())

        val detections = apriltagNative.apriltag_detect_yuv(image, bitmap.width, bitmap.height)
        if(detections.isNullOrEmpty()){
            Log.d("AprilTagDetection","Detections are null")
        }else{
            Log.d("AprilTagDetection","Detections are not null")
        }
        return detections?.getOrNull(0)
    }

    companion object {
        const val TYPE_000 = 0
    }
}
