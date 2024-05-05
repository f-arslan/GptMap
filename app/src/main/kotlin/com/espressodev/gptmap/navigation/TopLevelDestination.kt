package com.espressodev.gptmap.navigation

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
        GmIcons.BookmarkFilled,
        GmIcons.BookmarkOutlined,
        AppText.saved
    ),
    MAP(
        GmIcons.ExploreFilled,
        GmIcons.ExploreOutlined,
        AppText.explore
    ),
    SCREENSHOTGALLERY(
        GmIcons.ScreenshotFilled,
        GmIcons.ScreenshotOutlined,
        AppText.gallery
    )
}
