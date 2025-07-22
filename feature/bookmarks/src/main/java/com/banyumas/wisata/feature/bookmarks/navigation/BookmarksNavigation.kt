package com.banyumas.wisata.feature.bookmarks.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.feature.bookmarks.BookmarksScreen
import com.banyumas.wisata.feature.bookmarks.BookmarksViewModel
import kotlinx.serialization.Serializable

@Serializable
object BookmarksRoute

fun NavController.navigateToBookmarks(navOptions: NavOptions? = null) =
    navigate(route = BookmarksRoute, navOptions)

fun NavGraphBuilder.bookmarksScreen(
    onDestinationClick: (String) -> Unit,
) {
    composable<BookmarksRoute> {
        val viewModel = hiltViewModel<BookmarksViewModel>()
        val userViewModel = hiltViewModel<UserViewModel>()
        BookmarksScreen(
            navigateToDetail = {},
            userViewModel =
                userViewModel,
            viewmodel = viewModel
        )
    }
}