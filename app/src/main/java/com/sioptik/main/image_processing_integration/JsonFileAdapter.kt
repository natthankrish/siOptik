package com.sioptik.main.image_processing_integration

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class JsonFileAdapter {
    fun saveJsonFile(jsonTemplate: JsonTemplate, fileName: String, context: Context) {
        val dir = File(context.filesDir, "result-json")
        if (!dir.exists()) {
            dir.mkdir()
        }

        val file = File(dir, fileName)
        val jsonString = jsonTemplate.toString()
        val fileOutputStream = file.outputStream()
        fileOutputStream.use {
            it.write(jsonString.toByteArray())
        }
    }

    fun readJsonFile(fileName: String, context: Context): JsonTemplate? {
        val dir = File(context.filesDir, "result-json")
        if (!dir.exists()) {
            return null
        }

        val file = File(dir, fileName)
        if (!file.exists() || !file.isFile) {
            return null
        }

        val gsonBuilder = GsonBuilder().create()
        val type = object: TypeToken<HashMap<String, Int>>(){}.type
        val jsonString = file.inputStream().readBytes().toString()
        val result = gsonBuilder.fromJson<Map<String, Int>>(jsonString, type)
        val jsonTemplate = JsonTemplate(result.keys.toTypedArray())
        for (entry in result) {
            jsonTemplate.entry(entry.key, entry.value)
        }

        return jsonTemplate
    }

    fun generateFileName(apriltagId: Int): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        return "$currentTime apriltag_$apriltagId"
    }
}