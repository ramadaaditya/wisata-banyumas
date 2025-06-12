package com.banyumas.wisata.feature.bookmarks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.banyumas.wisata.feature.bookmarks.BookmarksScreen
import kotlinx.serialization.Serializable

@Serializable
object BookmarksRoute

fun NavController.navigateToBookmarks(navOptions: NavOptions? = null) =
    navigate(route = BookmarksRoute, navOptions)

fun NavGraphBuilder.bookmarksScreen(
    onDestinationClick: (String) -> Unit,
) {
    composable<BookmarksRoute> {
        BookmarksScreen(navigateToDetail = {})
    }
}