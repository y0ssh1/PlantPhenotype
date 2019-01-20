package yosshi.plantphenotyping.model

import android.app.Activity
import android.content.Intent

data class ActivityResultData(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
) {
    val isValid: Boolean
        get() = resultCode == Activity.RESULT_OK && data?.data != null
}