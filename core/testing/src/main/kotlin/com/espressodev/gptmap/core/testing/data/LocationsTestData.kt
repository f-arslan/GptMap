package com.espressodev.gptmap.core.testing.data

import com.espressodev.gptmap.core.model.Location


val locationsTestData: List<Location> = listOf(
    Location(
        id = "location1",
        content = contentsTestData[0],
        locationImages = locationImagesTestData,
        isAddedToFavourite = true,
        favouriteId = "fav1"
    ),
    Location(
        id = "location2",
        content = contentsTestData[1],
        locationImages = locationImagesTestData,
        isAddedToFavourite = false,
        favouriteId = "fav2"
    ),
    Location(
        id = "location3",
        content = contentsTestData[2],
        locationImages = locationImagesTestData,
        isAddedToFavourite = true,
        favouriteId = "fav3"
    ),
    Location(
        id = "location4",
        content = contentsTestData[3],
        locationImages = locationImagesTestData,
        isAddedToFavourite = false,
        favouriteId = "fav4"
    ),
    Location(
        id = "location5",
        content = contentsTestData[4],
        locationImages = locationImagesTestData,
        isAddedToFavourite = true,
        favouriteId = "fav5"
    )
)
