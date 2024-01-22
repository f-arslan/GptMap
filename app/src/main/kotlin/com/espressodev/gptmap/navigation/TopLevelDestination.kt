package com.espressodev.gptmap.navigation

import StreetView
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    @StringRes val contentDesc: Int,
) {
    FAVOURITE(
        GmIcons.FavouriteFilled,
        GmIcons.FavouriteOutlined,
        AppText.favourite
    ),
    MAP(
        GmIcons.ExploreFilled,
        GmIcons.ExploreOutlined,
        AppText.map
    ),
    SCREENSHOT_GALLERY(
        GmIcons.ScreenshotFilled,
        GmIcons.ScreenshotOutlined,
        AppText.screenshot_gallery
    )
}