package com.example.sioptik

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
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
        imageView= findViewById(R.id.processed_image)

        val imageUriString = intent.getStringExtra("image_uri") ?: return
        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)
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