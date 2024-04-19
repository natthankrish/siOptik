package com.sioptik.main.image_processing_integration

class JsonTemplateFactory {
    // TODO: Populate with real data
    private val apriltagJsonDictionary = mapOf<Int, JsonTemplate> (
        101 to JsonTemplate(arrayOf("field_a", "field_b", "field_c")),
        102 to JsonTemplate(arrayOf("caleg_1", "caleg_2", "caleg_3"))
    )
    fun jsonTemplate(apriltagId: Int): JsonTemplate {
        val template = apriltagJsonDictionary[apriltagId]
        if (template != null) {
            return template
        }
        throw Exception("No JSON template with apriltag ID $apriltagId")
    }
}