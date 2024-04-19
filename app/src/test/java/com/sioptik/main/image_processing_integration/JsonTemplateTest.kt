package com.sioptik.main.image_processing_integration

import org.junit.Test

class JsonTemplateTest {
    @Test
    fun testGson() {
        val jsonTemplateFactory = JsonTemplateFactory()

        val apriltag1 = 101
        val apriltag2 = 102

        val jsonTemplate1 = jsonTemplateFactory.jsonTemplate(101)
        jsonTemplate1.entry("field_a", 10)
        jsonTemplate1.entry("field_b", 20)
        jsonTemplate1.entry("field_c", 30)

        val jsonTemplate2 = jsonTemplateFactory.jsonTemplate(102)
        jsonTemplate2.entry("caleg_1", 100)
        jsonTemplate2.entry("caleg_2", 200)
        jsonTemplate2.entry("caleg_3", 300)

        println(jsonTemplate1.toString())
        println(jsonTemplate2.toString())
    }
}
