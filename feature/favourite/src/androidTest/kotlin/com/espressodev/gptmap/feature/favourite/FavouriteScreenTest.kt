package com.espressodev.gptmap.feature.favourite

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.espressodev.gptmap.core.common.util.makeRandomInstance
import com.espressodev.gptmap.core.model.Favourite
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test


class FavouriteScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun favouriteScreen_displaysFavourites() {
        val favourites = List(2) { makeRandomInstance<Favourite>() }

        composeTestRule.setContent {
            FavouriteScreen(
                favourites = favourites,
                selectedId = "",
                onCardClick = {},
                onLongClick = {},
                isUiInEditMode = false
            )
        }

        favourites.forEach { favourite ->
            composeTestRule.onNodeWithText(favourite.title).assertIsDisplayed()
            composeTestRule.onNodeWithText(favourite.placeholderTitle).assertIsDisplayed()
            composeTestRule.onNodeWithText(favourite.placeholderCoordinates).assertIsDisplayed()
        }
    }

    @Test
    fun favouriteScreen_clickingCardTriggersOnCardClick() {
        val favourites = generateRandomFavourites(5)
        var clickedId: String? = null
        composeTestRule.setContent {
            FavouriteScreen(
                favourites = favourites,
                selectedId = "",
                onCardClick = { clickedId = it },
                onLongClick = {},
                isUiInEditMode = false
            )
        }

        composeTestRule.onNodeWithText(favourites.first().title).performClick()

        assertTrue(clickedId == favourites.first().favouriteId)
    }


    @Test
    fun favouriteScreen_longClickingCardTriggersOnLongClick() {
        val favourites = generateRandomFavourites(4)
        var longClickedFavourite: Favourite? = null
        composeTestRule.setContent {
            FavouriteScreen(
                favourites = favourites,
                selectedId = "",
                onCardClick = {},
                onLongClick = { longClickedFavourite = it },
                isUiInEditMode = false
            )
        }
        composeTestRule.onNodeWithText(favourites.first().title).performTouchInput {
            longClick()
        }


        assertTrue(longClickedFavourite == favourites.first())
    }

    private fun generateRandomFavourites(number: Int): List<Favourite> =
        List(number) { makeRandomInstance() }
}
