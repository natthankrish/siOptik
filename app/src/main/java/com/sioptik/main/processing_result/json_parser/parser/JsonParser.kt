package com.sioptik.main.processing_result.json_parser.parser

import com.sioptik.main.processing_result.json_parser.model.Candidate
import com.sioptik.main.processing_result.json_parser.model.JsonData
import org.json.JSONObject

object JsonParser {
    fun parse(jsonString: String): JsonData {
        val jsonObject = JSONObject(jsonString)
        val title = jsonObject.getString("title")
        val description = jsonObject.getString("description")
        val aprilTagId = jsonObject.getInt("aprilTagId")
        val tpsId = jsonObject.getInt("tpsId")

        val candidatesJsonArray = jsonObject.getJSONArray("candidates")
        val candidatesList = mutableListOf<Candidate>()

        for (i in 0 until candidatesJsonArray.length()) {
            val candidateJsonObject = candidatesJsonArray.getJSONObject(i)
            val candidate = Candidate(
                orderNumber = candidateJsonObject.getInt("orderNumber"),
                choiceName = candidateJsonObject.getString("choiceName"),
                totalVoters = candidateJsonObject.getInt("totalVoters")
            )
            candidatesList.add(candidate)
        }

        return JsonData(
            title = title,
            description = description,
            aprilTagId = aprilTagId,
            tpsId = tpsId,
            candidates = candidatesList
        )
    }
}