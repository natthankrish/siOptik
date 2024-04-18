package com.sioptik.main.image_processing_integration

class JsonTemplate(
    val fieldNames: Array<String>,
    private val dictionary: HashMap<String, Int>
) {
    fun entry(fieldName: String, value: Int) {
        if (!fieldNames.contains(fieldName)) {
            throw Exception("Invalid Field Name")
        }
        dictionary[fieldName] = value
    }
}
