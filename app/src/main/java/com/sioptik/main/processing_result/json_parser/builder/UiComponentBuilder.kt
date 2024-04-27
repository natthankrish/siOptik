package com.sioptik.main.processing_result.json_parser.builder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.sioptik.main.R
import com.sioptik.main.image_processing_integration.JsonTemplate
import com.sioptik.main.processing_result.json_parser.model.Candidate
import com.sioptik.main.processing_result.json_parser.model.JsonData

object UiComponentBuilder {
    @SuppressLint("InflateParams", "MissingInflatedId")
    fun buildUi(jsonData: JsonData, context: Context): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_json, null, false)
        val titleTextView = rootView.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = rootView.findViewById<TextView>(R.id.descriptionTextView)
        val tpsIdTextView = rootView.findViewById<TextView>(R.id.tpsIdTextView)
        val aprilTagIdTextView = rootView.findViewById<TextView>(R.id.aprilTagIdTextView)
        updateSupportComponents(jsonData, titleTextView, descriptionTextView, tpsIdTextView, aprilTagIdTextView)

        val container = rootView.findViewById<LinearLayout>(R.id.resultsContainer)
        jsonData.candidates.forEach { candidate ->
            addCandidateView(candidate, container, context)
        }

        return rootView
    }

    fun buildUi(jsonTemplate: JsonTemplate, context: Context): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_json, null, false)
        val apriltagIdTextView = rootView.findViewById<TextView>(R.id.aprilTagIdTextView)
        val apriltagIdText = "Apriltag ID: ${jsonTemplate.apriltagId}"
        apriltagIdTextView.text = apriltagIdText

        val container = rootView.findViewById<LinearLayout>(R.id.resultsContainer)

        for (field in jsonTemplate.fieldNames) {
            addFieldView(field, jsonTemplate.get(field), container, context)
        }

        return rootView
    }

    private fun updateSupportComponents(jsonData: JsonData, titleTextView: TextView, descriptionTextView: TextView, tpsIdTextView: TextView, aprilTagTextView: TextView) {
        titleTextView.text = jsonData.title
        descriptionTextView.text = jsonData.description
        tpsIdTextView.text = jsonData.tpsId.toString()
        aprilTagTextView.text = jsonData.aprilTagId.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun addCandidateView(candidate: Candidate, container: LinearLayout, context: Context) {
        val titleTextView = LayoutInflater.from(context).inflate(R.layout.candidate_title_text_view, container, false) as TextView
        titleTextView.text = "No. Urut: ${candidate.orderNumber}, Name: ${candidate.choiceName}"

        val valueTextView = LayoutInflater.from(context).inflate(R.layout.candidate_value_text_view, container, false) as TextView
        valueTextView.text = "Jumlah Pemilih: ${candidate.totalVoters}"

        container.addView(titleTextView)
        container.addView(valueTextView)
    }

    private fun addFieldView(field: String, value: Int, container: LinearLayout, context: Context) {
        val titleTextView = LayoutInflater.from(context).inflate(R.layout.candidate_title_text_view, container, false) as TextView
        titleTextView.text = field

        val valueTextView = LayoutInflater.from(context).inflate(R.layout.candidate_value_text_view, container, false) as TextView
        valueTextView.text = value.toString()

        container.addView(titleTextView)
        container.addView(valueTextView)
    }
}