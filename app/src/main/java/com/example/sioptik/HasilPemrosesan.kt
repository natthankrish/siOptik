package com.example.sioptik

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.sioptik.processing_result.FullScreenImageActivity
import com.example.sioptik.processing_result.fragment_result_april_tag_types.AprilTagType000Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class HasilPemrosesan : AppCompatActivity() {
    private var loadingDialog: Dialog? = null
    private lateinit var imageView : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pemrosesan)
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(this))
        }
        imageView= findViewById(R.id.processed_image)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)
        val imageFile = uriToFile(this, imageUri, "temp_image.jpg")
        if(imageFile != null){
            processImage(imageFile.absolutePath)
        }
    }
    private fun processImage(imagePath: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            showLoadingDialog()
            val processedImage = withContext(Dispatchers.IO) {
                val python = Python.getInstance()
                val module = python.getModule("ocr")
                val result = module.callAttr("main", imagePath)
                result.toString()
            }
            hideLoadingDialog()
            val processedImageUri = Uri.fromFile(File(processedImage))
            imageView.setImageURI(processedImageUri)
        }
    }

    private fun showLoadingDialog() {
        loadingDialog = Dialog(this).apply {
            setContentView(R.layout.loading)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
    fun uriToFile(context: Context, uri: Uri, fileName: String): File? {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            return file
        }
        return null
    }
    companion object {
        const val TYPE_000 = 0
    }
}