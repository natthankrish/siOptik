package com.sioptik.main.processing_result.json_parser.model

data class JsonData(
    val title: String,
    val description: String,
    val aprilTagId: Int,
    val tpsId: Int,
    val candidates: MutableList<Candidate>
)

data class Candidate(
    val orderNumber: Int,
    val choiceName: String,
    val totalVoters: Int
)
