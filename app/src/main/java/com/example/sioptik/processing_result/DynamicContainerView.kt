package com.example.sioptik.processing_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sioptik.processing_result.json_parser.builder.UiComponentBuilder

class DynamicContentFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val jsonData = viewModel.jsonData
        return jsonData?.let { UiComponentBuilder.buildUi(it, requireContext()) }
    }
}
