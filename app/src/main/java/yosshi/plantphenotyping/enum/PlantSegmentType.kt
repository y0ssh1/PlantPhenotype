package yosshi.plantphenotyping.enum

enum class PlantSegmentType(val value: String) {
    FLOWER("flower"),
    FRUIT("fruit"),
    GREEN("green"),
    NONE("");

    val labelName: String
        get() = when(this) {
            FLOWER -> "花"
            FRUIT -> "実"
            GREEN -> "葉・茎"
            NONE -> "その他"
        }

    companion object {
        @JvmStatic
        fun fromValue(value: String): PlantSegmentType {
            return PlantSegmentType.values().find { it.value == value } ?: NONE
        }
    }
}