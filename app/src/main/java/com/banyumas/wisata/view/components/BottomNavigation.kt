package com.banyumas.wisata.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme

data class NavigationItem(
    val icon: ImageVector,
    val screen: String,
    val contentDescription: String,
    val title: String,
)

private val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Default.Home,
        screen = Screen.Home.route,
        contentDescription = "Home"
    ),
    NavigationItem(
        title = "Favorite",
        icon = Icons.Default.Favorite,
        screen = Screen.FavoriteScreen.route,
        contentDescription = "Favorite"
    ),
    NavigationItem(
        title = "Profile",
        icon = Icons.Default.Person,
        screen = Screen.ProfileScreen.route,
        contentDescription = "Profile"
    )
)

@Composable
fun BottomNavigation(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.screen) {
                        navController.navigate(item.screen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription,
                    )
                },
                label = {
                    Text(item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BottomNavigationPreview() {
    AppTheme {
        val navController = rememberNavController()
        BottomNavigation(navController = navController)
    }
}