package com.example.sioptik

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.sioptik.fragment_result_april_tag_types.AprilTagType000Fragment

class HasilPemrosesan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)

        val frameLayout = findViewById<FrameLayout>(R.id.imagePlaceholderRight)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
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
