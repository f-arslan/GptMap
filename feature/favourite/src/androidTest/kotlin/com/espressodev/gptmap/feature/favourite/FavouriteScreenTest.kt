package com.espressodev.gptmap.feature.favourite

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import com.espressodev.gptmap.core.common.random_gen.makeRandomInstance
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

    @Test
    fun favouriteScreen_scrollsCorrectlyWithManyFavourites() {
        val manyFavourites = List(10) {
          Favourite(id = it.toString())
        }
        manyFavourites.last().also(::println)

        composeTestRule.setContent {
            FavouriteScreen(
                favourites = manyFavourites,
                selectedId = "",
                onCardClick = {},
                onLongClick = {},
                isUiInEditMode = false
            )
        }

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("FavouriteScreen")
        composeTestRule.onNodeWithTag(LazyColumnTestTag, useUnmergedTree = true)
            .onChildren()
            .onFirst()
            .assert(hasText(manyFavourites.first().title))
    }


    @Test
    fun favouriteScreen_restoresStateAfterRotation() {
        val favourites = generateRandomFavourites(5)
        composeTestRule.setContent {
            FavouriteScreen(
                favourites = favourites,
                selectedId = "",
                onCardClick = {},
                onLongClick = {},
                isUiInEditMode = false
            )
        }

        // Save the state of the UI
        val state = composeTestRule.onRoot().captureToImage().asAndroidBitmap()

        // Simulate a configuration change, such as a device rotation
        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Wait for the UI to stabilize after the configuration change
        composeTestRule.waitForIdle()

        // Restore the state of the UI
        composeTestRule.onRoot().captureToImage().prepareToDraw()

    }



    private fun generateRandomFavourites(number: Int): List<Favourite> =
        List(number) { makeRandomInstance() }
}
