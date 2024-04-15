package com.sioptik.main.apriltag_detection

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.objdetect.ArucoDetector
import org.opencv.objdetect.DetectorParameters
import org.opencv.objdetect.Objdetect

class ApriltagDetector {
    fun detect(bitmap: Bitmap) {
        val imageMat = Mat()
        val corners = listOf<Mat>()
        val ids = Mat()
        Utils.bitmapToMat(bitmap, imageMat)
        val dictionary = Objdetect.getPredefinedDictionary(Objdetect.DICT_APRILTAG_36h10)
        val detectorParameters = DetectorParameters()
        val arucoDetector = ArucoDetector(dictionary, detectorParameters)
        arucoDetector.detectMarkers(imageMat, corners, ids)
        Log.d("ApriltagDetector", "ID found: ${ids[0, 0]}")
    }
}