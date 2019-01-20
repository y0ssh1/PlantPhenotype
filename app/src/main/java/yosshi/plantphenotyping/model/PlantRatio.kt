package yosshi.plantphenotyping.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import yosshi.plantphenotyping.enum.PlantSegmentType

data class PlantRatio(
    @SerializedName("segment_type")
    val segmentTypeString: String,
    val count: Float
) {
    val segmentType: PlantSegmentType
        get() = PlantSegmentType.fromValue(segmentTypeString)
}