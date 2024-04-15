package com.example.sioptik.apriltag;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class AprilTagTest {
    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // You can choose other formats like JPEG or PNG
        return stream.toByteArray();
    }

    byte [] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];

        // Ensure that the bitmap is in ARGB_8888 format
        scaled = scaled.copy(Bitmap.Config.ARGB_8888, true);

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        try {
            final int frameSize = width * height;

            int yIndex = 0;
            int uvIndex = frameSize;

            int a, R, G, B, Y, U, V;
            int index = 0;
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {

                    a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                    R = (argb[index] & 0xff0000) >> 16;
                    G = (argb[index] & 0xff00) >> 8;
                    B = (argb[index] & 0xff) >> 0;

                    // well known RGB to YUV algorithm
                    Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                    U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                    V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                    // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                    //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                    //    pixel AND every other scanline.
                    yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                    if (j % 2 == 0 && index % 2 == 0) {
                        yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                        yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                    }

                    index ++;
                }
            }

        } catch (IndexOutOfBoundsException e) {

        }

    }
    @Test
    public void testAprilTagInit() {
        AprilTagNative.apriltag_init("tag36h10", 0, 8, 0, 4);
        assertTrue(true);
    }

    @Test
    public void testImport() {
        Context context = ApplicationProvider.getApplicationContext();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream inputStream = assetManager.open("test9.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] byteArray = getNV21(width, height, bitmap);
//            byte[] byteArray = getNV21(width, height, bitmap);
            Log.i("BUFFER", String.valueOf(byteArray.length) + " " +  width + " " + height);
            AprilTagNative.apriltag_init("tag36h10", 2, 1, 0, 4);
            ArrayList<AprilTagDetection> detections = AprilTagNative.apriltag_detect_yuv(byteArray, width, height);
            for (AprilTagDetection detection : detections) {
                Log.i("ID", String.valueOf(detection.id));
            }
            Log.i("DETECTION LENGTH", String.valueOf(detections.size()));
            inputStream.close();
        } catch (IOException e) {
            Log.e("TEST", "MASUK SINI");
            e.printStackTrace();
        }
    }


}
