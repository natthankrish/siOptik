package com.example.sioptik

import android.graphics.Bitmap


class ApriltagNative {
    init {
        System.loadLibrary("apriltag")
        native_init()
    }

    external fun native_init()
    external fun yuv_to_rgb(src: ByteArray?, width: Int, height: Int, dst: Bitmap?)
    external fun apriltag_init(
        tagFamily: String?, errorBits: Int, decimateFactor: Double,
        blurSigma: Double, nthreads: Int
    )

    external fun apriltag_detect_yuv(
        src: ByteArray?,
        width: Int,
        height: Int
    ): ArrayList<ApriltagDetection?>?
}
