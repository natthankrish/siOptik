package com.sioptik.main.processing_result

import androidx.lifecycle.ViewModel
import com.sioptik.main.processing_result.json_parser.model.JsonData

class SharedViewModel : ViewModel() {
    var jsonData: JsonData? = null
}
