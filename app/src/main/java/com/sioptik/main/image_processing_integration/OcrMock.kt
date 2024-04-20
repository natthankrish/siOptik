package com.sioptik.main.image_processing_integration

import android.graphics.Bitmap
import kotlin.random.Random

class OcrMock {
    fun detect(bitmap: Bitmap?, apriltagId: Int): JsonTemplate {
        val jsonTemplate = JsonTemplateFactory().jsonTemplate(apriltagId)
        for (field in jsonTemplate.fieldNames) {
            jsonTemplate.entry(field, Random.nextInt(1000))
        }
        return jsonTemplate
    }
}