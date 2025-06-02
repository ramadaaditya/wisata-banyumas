package com.banyumas.wisata.core.designsystem.components

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

fun NavGraphBuilder.addHomeGraph(
    onDestinationSelected: (placeId: String, from: NavBackStackEntry) -> Unit,
    userViewModel: UserViewModel,
    destinationViewModel: DestinationViewModel,
    modifier: Modifier = Modifier,
    userId: String,
) {
    composable(HomeSection.FEED.route) { from ->
        HomeScreen(
            navigateToDetail = { placeId ->
                onDestinationSelected(placeId, from)
            },
            onFavoriteClick = {
            },
            userViewModel = userViewModel,
            destinationViewModel = destinationViewModel,
            userId = userId,
        )
    }
    composable(HomeSection.FAVORITE.route) { from ->
        FavoriteScreen(
            navigateToDetail = { placeId ->
                onDestinationSelected(placeId, from)
            },
        )
    }
    composable(HomeSection.PROFILE.route) {
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
            val selected = currentRoute.startsWith(section.route)
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