package yosshi.plantphenotyping.model

data class ImageResponseBody(
    val ratio: List<PlantRatio>,
    val segmentImgUrl: String,
    val yoloImgUrl: String,
    val flowerCount: Int,
    val fruitCount: Int
) {
    val images: List<String>
        get() = listOf(yoloImgUrl, segmentImgUrl)
}