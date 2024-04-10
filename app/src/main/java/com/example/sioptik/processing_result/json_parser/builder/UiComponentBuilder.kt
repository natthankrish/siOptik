package com.example.sioptik.processing_result.json_parser.builder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sioptik.R
import com.example.sioptik.processing_result.json_parser.model.Candidate
import com.example.sioptik.processing_result.json_parser.model.JsonData

object UiComponentBuilder {
    @SuppressLint("InflateParams", "MissingInflatedId")
    fun buildUi(jsonData: JsonData, context: Context): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_json, null, false)
        val titleTextView = rootView.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = rootView.findViewById<TextView>(R.id.descriptionTextView)
        updateTitleAndDescription(jsonData, titleTextView, descriptionTextView)

        val container = rootView.findViewById<LinearLayout>(R.id.resultsContainer)
        jsonData.candidates.forEach { candidate ->
            addCandidateView(candidate, container, context)
        }

        return rootView
    }

    private fun updateTitleAndDescription(jsonData: JsonData, titleTextView: TextView, descriptionTextView: TextView) {
        titleTextView.text = jsonData.title
        descriptionTextView.text = jsonData.description
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
}