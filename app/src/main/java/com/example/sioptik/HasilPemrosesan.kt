package com.example.sioptik

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.sioptik.processing_result.FullScreenImageActivity
import com.example.sioptik.processing_result.fragment_result_april_tag_types.AprilTagType000Fragment

class HasilPemrosesan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val imageView: ImageView = findViewById(R.id.processed_image)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)

        imageView.setImageURI(imageUri)

        imageView.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("imageUri", imageUriString)
            startActivity(intent)
        }

        val aprilTagType = intent.getIntExtra("april_tag_type", 0)

        supportFragmentManager.commit {
            when (aprilTagType) {
                TYPE_000 -> replace(R.id.fragmentContainerView, AprilTagType000Fragment())
            }
        }
    }

    companion object {
        const val TYPE_000 = 0
    }
}