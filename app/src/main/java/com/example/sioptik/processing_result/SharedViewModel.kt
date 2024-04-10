package com.example.sioptik.processing_result

import androidx.lifecycle.ViewModel
import com.example.sioptik.processing_result.json_parser.model.JsonData

class SharedViewModel : ViewModel() {
    var jsonData: JsonData? = null
}
