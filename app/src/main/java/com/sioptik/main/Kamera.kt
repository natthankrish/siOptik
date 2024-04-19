package com.sioptik.main

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sioptik.main.camera_processor.CameraProcessor
import com.sioptik.main.databinding.KameraBinding
import com.sioptik.main.image_processor.ImageProcessor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Kamera : AppCompatActivity() {
    private lateinit var viewBinding: KameraBinding
    private val MAX_WIDTH = 1600;

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = KameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewBinding.captureButton.setOnClickListener { takePhoto() }
        viewBinding.pickImage.setOnClickListener { pickImageFromGallery() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val viewFinder = findViewById<PreviewView>(R.id.viewFinder)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val cameraProcessor = CameraProcessor()
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            data?.data?.let { uri ->

                val isTooLarge = cameraProcessor.isImageTooWide(contentResolver, uri, MAX_WIDTH)
                if (isTooLarge){
                    Toast.makeText(this, "The Image is scaled down", Toast.LENGTH_LONG).show()
                    val scaledBitmap = cameraProcessor.scaleDownImage(contentResolver, uri, MAX_WIDTH)

                    val tempFile = cameraProcessor.createTempFile(this@Kamera, "GALLERY")
                    cameraProcessor.saveBitmapToFile(scaledBitmap, tempFile)

                    val savedUri = FileProvider.getUriForFile(
                        this@Kamera,
                        "com.sioptik.main.provider",
                        tempFile
                    )
                    processImageUri(savedUri)

                } else {
                    processImageUri(uri)
                }

            } ?: Toast.makeText(this, "Error: No image selected!", Toast.LENGTH_LONG).show()
        }
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val cameraProcessor = CameraProcessor()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val bitmap = cameraProcessor.imageProxyToBitmap(image)
                    val scaledBitmap = cameraProcessor.scaleDownBitmap(bitmap!!, MAX_WIDTH)

                    image.close()

                    val tempFile = cameraProcessor.createTempFile(this@Kamera, "CAMERA")
                    cameraProcessor.saveBitmapToFile(scaledBitmap, tempFile)

                    val savedUri = FileProvider.getUriForFile(
                        this@Kamera,
                        "com.sioptik.main.provider",
                        tempFile
                    )

                    processImageUri(savedUri)
                }
            }
        )
    }

    private fun processImageUri(imageUri: Uri) {
        Intent(this@Kamera, ValidasiGambar::class.java).also { previewIntent ->
            previewIntent.putExtra("image_uri", imageUri.toString())
            startActivity(previewIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        val IMAGE_REQUEST_CODE = 100

    }
}
