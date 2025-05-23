package com.banyumas.wisata.view.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.banyumas.wisata.R
import com.banyumas.wisata.view.home.FavoriteScreen
import com.banyumas.wisata.view.home.HomeScreen
import com.banyumas.wisata.view.home.ProfileScreen
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme


fun NavGraphBuilder.addHomeGraph(
    onDestinationSelected: (placeId: String, from: NavBackStackEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    composable(HomeSection.FEED.route) { from ->
        HomeScreen(
            navigateToDetail = { placeId ->
                onDestinationSelected(placeId, from)
            },
            onFavoriteClick = {
            },
        )
    }
    composable(HomeSection.FAVORITE.route) { from ->
        FavoriteScreen(
            navigateToDetail = {
            }
        )
    }
    composable(HomeSection.PROFILE.route) { from ->
        ProfileScreen(
            onLogout = {},
            onDelete = {}
        )
    }
}

@Composable
fun WBBottomBar(
    tabs: Array<HomeSection>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = BanyumasTheme.colors.primary,
    contentColor: Color = BanyumasTheme.colors.secondary
) {
    NavigationBar(
        modifier = modifier,
        containerColor = color,
        contentColor = contentColor
    ) {
        tabs.forEach { section ->
            val selected = section.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navigateToRoute(section.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = stringResource(id = section.title)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = section.title),
                        style = BanyumasTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BanyumasTheme.colors.primary,
                    unselectedIconColor = BanyumasTheme.colors.onSurfaceVariant,
                    indicatorColor = BanyumasTheme.colors.primaryContainer,
                    selectedTextColor = BanyumasTheme.colors.primary,
                    unselectedTextColor = BanyumasTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}


enum class HomeSection(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),
    FAVORITE(R.string.home_favorite, Icons.Outlined.Favorite, "home/favorite"),
    PROFILE(R.string.home_profile, Icons.Outlined.Person, "home/profile")
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationPreview() {
    WisataBanyumasTheme {
        WBBottomBar(
            tabs = HomeSection.entries.toTypedArray(),
            currentRoute = "home/feed",
            navigateToRoute = {}
        )
    }
}