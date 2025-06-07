package com.banyumas.wisata.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.banyumas.wisata.core.designsystem.icon.WbIcons
import com.banyumas.wisata.R
import com.banyumas.wisata.feature.profile.R as proR
import com.banyumas.wisata.feature.dashboard.R as dashR
import com.banyumas.wisata.feature.bookmarks.R as bookR

import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: String,
) {
    DASHBOARD(
        selectedIcon = WbIcons.Dashboard,
        unselectedIcon = WbIcons.DashboardOutline,
        iconTextId = dashR.string.feature_profile_title,
        titleTextId = dashR.string.feature_profile_title,
        route = "dashboard_graph",
    ),
    BOOKMARKS(
        selectedIcon = WbIcons.Bookmarks,
        unselectedIcon = WbIcons.BookmarksBorder,
        iconTextId = bookR.string.feature_profile_title,
        titleTextId = bookR.string.feature_profile_title,
        route = "bookmarks_graph",
    ),
    PROFILE(
        selectedIcon = WbIcons.Person,
        unselectedIcon = WbIcons.PersonOutline,
        iconTextId = proR.string.feature_profile_title,
        titleTextId = proR.string.feature_profile_title,
        route = "profile_screen",
    )
}