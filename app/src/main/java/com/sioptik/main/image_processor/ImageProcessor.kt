package com.sioptik.main.image_processor

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ImageProcessor {

    fun preprocessImage(srcMat: Mat): Mat {
        val grayMat = convertToGray(srcMat)
        val blurredMat = applyGaussianBlur(grayMat)
        val thresholdMat = applyAdaptiveThreshold(blurredMat)
        return applyMorphologicalOperations(thresholdMat)
    }

    private fun convertToGray(colorMat: Mat): Mat {
        val grayMat = Mat()
        Imgproc.cvtColor(colorMat, grayMat, Imgproc.COLOR_RGB2GRAY)
        return grayMat
    }

    private fun applyGaussianBlur(grayMat: Mat): Mat {
        val blurredMat = Mat()
        Imgproc.GaussianBlur(grayMat, blurredMat, Size(5.0, 5.0), 0.0)
        return blurredMat
    }

    private fun applyAdaptiveThreshold(blurredMat: Mat): Mat {
        val thresholdMat = Mat()
        Imgproc.adaptiveThreshold(blurredMat, thresholdMat, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2.0)
        return thresholdMat
    }

    private fun applyMorphologicalOperations(thresholdMat: Mat): Mat {
        val morphMat = Mat()
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.dilate(thresholdMat, morphMat, element)
        Imgproc.erode(morphMat, morphMat, element)
        return morphMat
    }

    private fun convertMatToBitmap(processedMat: Mat): Bitmap {
        val resultBitmap = Bitmap.createBitmap(processedMat.cols(), processedMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(processedMat, resultBitmap)
        return resultBitmap
    }

    fun convertBitmapToMat(bitmap: Bitmap): Mat {
        val originalMat = Mat()
        Utils.bitmapToMat(bitmap, originalMat)
        return originalMat
    }

    fun detectRectangles(processedMat: Mat): List<Rect> {
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(processedMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        val rectangles = mutableListOf<Rect>()

        contours.forEach {
            val contourPoly = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*it.toArray()), contourPoly, 3.0, true)
            if (contourPoly.total() == 4L) {
                val rect = Imgproc.boundingRect(MatOfPoint(*contourPoly.toArray()))
                rectangles.add(rect)
            }
        }
        return rectangles
    }

    fun visualizeContoursAndRectangles(processedMat: Mat, rectangles: List<Rect>): Bitmap {
        // Create a copy of the processed image to draw on
        val visualizedImage = Mat()
        Imgproc.cvtColor(processedMat, visualizedImage, Imgproc.COLOR_GRAY2BGR)

        // Draw all detected rectangles
        rectangles.forEach { rect ->
            Imgproc.rectangle(visualizedImage, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 2) // Draw in green
        }

        return this.convertMatToBitmap(visualizedImage)
    }
}