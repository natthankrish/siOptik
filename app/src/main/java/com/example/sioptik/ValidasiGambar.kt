package com.example.sioptik

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ValidasiGambar : AppCompatActivity() {
    private var loadingDialog: Dialog? = null
    private lateinit var frameLayout: FrameLayout
    private var processedImageUri : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.validasi_gambar)
        frameLayout = findViewById(R.id.imageValidationContainer)
        if(!Python.isStarted()){
            Python.start(AndroidPlatform(this))
        }
        val imageUriString = intent.getStringExtra("image_uri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            val imageFile = uriToFile(this, imageUri,"temp_image.jpg")
            if(imageFile != null){
                processImage(imageFile.absolutePath)
            }
        } else {
            // Handle case where imageUriString is null
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
                previewIntent.putExtra("image_uri", processedImageUri)
                startActivity(previewIntent)
            }
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
            val imageView = ImageView(this@ValidasiGambar).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER // Ubah ke FIT_CENTER
                val imageUri = Uri.fromFile(File(processedImage))
                processedImageUri = imageUri.toString()
                setImageURI(imageUri) // Menetapkan URI gambar yang diproses
            }
            frameLayout.addView(imageView) // Jangan lupa menambahkan imageView ke frameLayout
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
}
