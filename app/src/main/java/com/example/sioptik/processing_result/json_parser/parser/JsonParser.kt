package com.example.sioptik.processing_result.json_parser.parser

import com.example.sioptik.processing_result.json_parser.model.JsonData
import org.json.JSONObject

object JsonParser {
    fun parse(jsonString: String): JsonData {
        val jsonObject = JSONObject(jsonString)
        return JsonData(
            title = jsonObject.getString("title"),
            description = jsonObject.getString("description"),
        )
    }
}