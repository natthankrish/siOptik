package com.sioptik.main.image_processor

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
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

    fun convertToGray(colorMat: Mat): Mat {
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
        Imgproc.adaptiveThreshold(
            blurredMat,
            thresholdMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            11,
            2.0
        )
        return thresholdMat
    }

    private fun applyMorphologicalOperations(thresholdMat: Mat): Mat {
        val morphMat = Mat()
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.dilate(thresholdMat, morphMat, element)
        Imgproc.erode(morphMat, morphMat, element)
        return morphMat
    }

    fun convertMatToBitmap(processedMat: Mat): Bitmap {
        val resultBitmap =
            Bitmap.createBitmap(processedMat.cols(), processedMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(processedMat, resultBitmap)
        Log.i("TEST", resultBitmap.width.toString() + "|| " + resultBitmap.height.toString())
        return resultBitmap
    }

    fun convertBitmapToMat(bitmap: Bitmap): Mat {
        val originalMat = Mat()
        Utils.bitmapToMat(bitmap, originalMat)
        return originalMat
    }

    fun detectBoxes(processedMat: Mat): List<Rect> {
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            processedMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        val squares = mutableListOf<Rect>()
        val aspect_threshold = 0.1
        val width_threshold = 50

        contours.forEach {
            val contourPoly = MatOfPoint2f()
            Imgproc.approxPolyDP(MatOfPoint2f(*it.toArray()), contourPoly, 3.0, true)
            if (contourPoly.total() == 4L) {
                val rect = Imgproc.boundingRect(MatOfPoint(*contourPoly.toArray()))
                val aspectRatio = rect.width.toDouble() / rect.height.toDouble()
                if (aspectRatio >= (1 - aspect_threshold) && aspectRatio <= (1 + aspect_threshold) && rect.width > width_threshold) {
//                    Log.i ("BOX", rect.toString() + "||" + rect.width.toString() + "||" + rect.height.toString())
                    squares.add(rect)
                }
            }
        }
        return squares
    }

//    fun detectRectangles(processedMat: Mat): List<Rect> {
//        val contours = ArrayList<MatOfPoint>()
//        val hierarchy = Mat()
//        Imgproc.findContours(processedMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
//        val rectangles = mutableListOf<Rect>()
//
//        contours.forEach {
//            val contourPoly = MatOfPoint2f()
//            Imgproc.approxPolyDP(MatOfPoint2f(*it.toArray()), contourPoly, 3.0, true)
//            if (contourPoly.total() == 4L) {
//                val rect = Imgproc.boundingRect(MatOfPoint(*contourPoly.toArray()))
//                rectangles.add(rect)
//            }
//        }
//        return rectangles
//    }

    fun detectColorSpace(mat: Mat): String {
        val numChannels = mat.channels()

        return when (numChannels) {
            1 -> "Grayscale"
            3 -> "BGR"
            4 -> "BGRA"
            else -> "Unknown"
        }
    }


    fun visualizeContoursAndRectangles(
        processedMat: Mat,
        rectangles: List<Rect>,
        color: String
    ): Mat {
        if (!rectangles.isEmpty()) {
            // Create a copy of the processed image to draw on
            val visualizedImage = processedMat

            // Havent handled for RGBA and Unkown
            if (detectColorSpace(visualizedImage) == "Grayscale") {
                Imgproc.cvtColor(visualizedImage, visualizedImage, Imgproc.COLOR_GRAY2BGR)
            }


            var colorScale = Scalar(0.0, 0.0, 0.0)
            if (color == "G") {
                colorScale = Scalar(0.0, 255.0, 0.0)
            } else if (color == "R") {
                colorScale = Scalar(255.0, 0.0, 0.0)
            } else if (color == "B") {
                colorScale = Scalar(0.0, 0.0, 255.0)
            }

            // Draw all detected rectangles
            rectangles.forEach { rect ->
//                Log.i("TEST", rect.tl().toString() + "||" + rect.br().toString())
                Imgproc.rectangle(
                    visualizedImage,
                    rect.tl(),
                    rect.br(),
                    colorScale,
                    8
                ) // Draw Rectangle
            }

            Log.i("TEST", "HEHE" + visualizedImage.toString())
            return visualizedImage
        } else {
            return processedMat
        }
    }

    fun loadDrawableImage(context: Context, drawableId: Int): Mat {
        // Load the drawable image as a Bitmap
        val drawable = context.resources.getDrawable(drawableId, null)
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Convert Bitmap to OpenCV Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        return mat
    }

//    fun detectBorders(imageMat: Mat, borderImage: Mat): List<Rect> {
//        val result = Mat()
//        Imgproc.matchTemplate(imageMat, borderImage, result, Imgproc.TM_CCOEFF_NORMED)
//        Core.normalize(result, result, 0.0, 1.0, Core.NORM_MINMAX, -1, Mat())
//
//        val threshold = 0.8 // Adjust threshold as needed
//        val locations = Mat()
//        Core.findNonZero(result, locations)
//        Log.i("TEST", locations.toString())
//
//        val rects = mutableListOf<Rect>()
//        for (loc in 0 until locations.rows()) {
//            val matchLoc = locations[0, loc]
//            if (matchLoc != null) {
//                val matchLocPt = Point(matchLoc[0], matchLoc[1])
//                val rect = Rect(matchLocPt, Size(borderImage.cols().toDouble(), borderImage.rows().toDouble()))
//                rects.add(rect)
//            }
//        }
//        return rects
//    }

    fun detectBorders(mainImage: Mat, templateImage: Mat): List<Rect> {
        val result = Mat()
        Imgproc.matchTemplate(mainImage, templateImage, result, Imgproc.TM_CCOEFF_NORMED)

        // Set a threshold for the matching result
        val threshold = 0.8
        val matches = mutableListOf<Rect>()

        val w = templateImage.cols()
        val h = templateImage.rows()

        // Find template matches
        val mmr = Core.minMaxLoc(result)
        if (mmr.maxVal >= threshold) {
            // Draw rectangle around best match
            val matchLoc = mmr.maxLoc
            matches.add(Rect(matchLoc.x.toInt(), matchLoc.y.toInt(), w, h))

        }
        return matches
    }

}
