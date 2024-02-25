package com.espressodev.gptmap.core.testing.data

import com.espressodev.gptmap.core.model.unsplash.LocationImage

val locationImagesTestData: List<LocationImage> = List(5) {
    LocationImage(
        id = "img${it + 1}",
        analysisId = "analysis${it + 1}",
        imageUrl = "https://example.com/image${it + 1}.jpg",
        imageAuthor = "Author ${it + 1}"
    )
}
