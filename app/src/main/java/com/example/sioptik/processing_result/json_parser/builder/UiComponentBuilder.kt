package com.example.sioptik.processing_result.json_parser.builder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.sioptik.R
import com.example.sioptik.processing_result.json_parser.model.JsonData

object UiComponentBuilder {
    @SuppressLint("InflateParams")
    fun buildUi(jsonData: JsonData, context: Context): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_json, null, false)
        val titleTextView = rootView.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = rootView.findViewById<TextView>(R.id.descriptionTextView)
        updateComponents(jsonData, titleTextView, descriptionTextView)
        return rootView
    }

    private fun updateComponents(jsonData: JsonData, titleTextView: TextView, descriptionTextView: TextView) {
        titleTextView.text = jsonData.title
        descriptionTextView.text = jsonData.description
    }
}