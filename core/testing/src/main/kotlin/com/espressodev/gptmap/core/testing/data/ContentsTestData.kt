package com.espressodev.gptmap.core.testing.data

import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.chatgpt.Coordinates


val contentsTestData: List<Content> = listOf(
    Content(
        coordinates = Coordinates(41.579561, 32.317705),
        city = "Istanbul",
        district = "Şişli",
        country = "Turkey",
        poeticDescription = "A city that never sleeps",
        normalDescription = "A major cultural and financial hub"
    ),
    Content(
        coordinates = Coordinates(36.886586, 38.291102),
        city = "Istanbul",
        district = "Şişli",
        country = "Turkey",
        poeticDescription = "A city that never sleeps",
        normalDescription = "Known for its rich history"
    ),
    Content(
        coordinates = Coordinates(41.835282, 35.220449),
        city = "Istanbul",
        district = "Beşiktaş",
        country = "Turkey",
        poeticDescription = "Where history and modernity blend seamlessly",
        normalDescription = "A major cultural and financial hub"
    ),
    Content(
        coordinates = Coordinates(38.535052, 38.647072),
        city = "Istanbul",
        district = "Şişli",
        country = "Turkey",
        poeticDescription = "A city that never sleeps",
        normalDescription = "A major cultural and financial hub"
    ),
    Content(
        coordinates = Coordinates(37.238799, 27.496014),
        city = "Istanbul",
        district = "Sarıyer",
        country = "Turkey",
        poeticDescription = "Where continents meet",
        normalDescription = "A city with a vibrant street life"
    )
)
