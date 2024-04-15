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
import com.sioptik.main.databinding.KameraBinding
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
    private val MAX_WIDTH = 2400;

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
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            data?.data?.let { uri ->

                val isTooLarge = isImageTooWide(contentResolver, uri, MAX_WIDTH)
                if (isTooLarge){
                    Toast.makeText(this, "The Image is scaled down", Toast.LENGTH_LONG).show()
                    val scaledBitmap = scaleDownImage(contentResolver, uri, MAX_WIDTH)

                    val tempFile = createTempFile()
                    saveBitmapToFile(scaledBitmap, tempFile)

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

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val bitmap = imageProxyToBitmap(image)
                    image.close()

                    val tempFile = createTempFile()
                    saveBitmapToFile(bitmap, tempFile)

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

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun saveBitmapToFile(bitmap: Bitmap?, file: File) {
        FileOutputStream(file).use { out ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun createTempFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            deleteOnExit()
        }
    }

    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            // Use ContentResolver to open an InputStream from the URI
            val inputStream = contentResolver.openInputStream(uri)
            // Decode the InputStream into a Bitmap using BitmapFactory
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            null
        }
    }

    fun isImageTooWide(contentResolver: ContentResolver, uri: Uri, maxWidth: Int): Boolean {
        return try {
            // Use ContentResolver to open an InputStream from the URI
            val inputStream = contentResolver.openInputStream(uri)
            // Decode only the image bounds without loading the full image into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            // Close the InputStream
            inputStream?.close()

            // Check if the width exceeds the maximum width threshold
            options.outWidth > maxWidth
        } catch (e: IOException) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            false // Return false in case of an error
        }
    }

    // Function to make the image smaller by scaling down while preserving aspect ratio
    fun scaleDownImage(contentResolver: ContentResolver, uri: Uri, maxWidth: Int): Bitmap? {
        return try {
            // Use ContentResolver to open an InputStream from the URI
            val inputStream = contentResolver.openInputStream(uri)
            // Decode the image file to get its dimensions
            val options = BitmapFactory.Options().apply {
                // Set inJustDecodeBounds to true to get the dimensions of the image without loading it into memory
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            // Close the InputStream
            inputStream?.close()

            // Calculate the sample size based on the maximum width
            options.inSampleSize = calculateSampleSize(options, maxWidth)

            // Decode the image file with the calculated sample size
            options.inJustDecodeBounds = false
            val scaledBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)

            // Optionally rotate the Bitmap if necessary
            // val rotatedBitmap = rotateBitmap(scaledBitmap, getRotationAngle(contentResolver, uri))

            scaledBitmap // Return the scaled and rotated Bitmap
        } catch (e: IOException) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            null // Return null in case of an error
        }
    }

    // Function to calculate the sample size for scaling down the image
    private fun calculateSampleSize(options: BitmapFactory.Options, maxWidth: Int): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > maxWidth) {
            // Calculate the sample size to reduce the width to fit within the maximum width
            inSampleSize = Math.ceil((width.toFloat() / maxWidth.toFloat()).toDouble()).toInt()
        }
        return inSampleSize
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
