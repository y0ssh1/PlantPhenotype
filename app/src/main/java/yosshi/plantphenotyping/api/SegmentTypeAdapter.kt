package yosshi.plantphenotyping.api

import com.google.gson.*
import yosshi.plantphenotyping.enum.PlantSegmentType
import java.lang.reflect.Type

class SegmentTypeAdapter {
    companion object: JsonSerializer<PlantSegmentType?>, JsonDeserializer<PlantSegmentType?> {
        override fun serialize(src: PlantSegmentType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src?.value ?: "")
        }
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): PlantSegmentType {
            return PlantSegmentType.fromValue(json?.asString ?: "")
        }
    }
}