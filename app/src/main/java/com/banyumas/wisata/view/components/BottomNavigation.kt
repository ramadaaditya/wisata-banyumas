package com.banyumas.wisata.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.banyumas.wisata.view.navigation.NavigationItem
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun BottomNavigation(
    navController: NavHostController,
) {
    NavigationBar(
        containerColor = AppTheme.colorScheme.background,
        contentColor = AppTheme.colorScheme.onBackground
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Default.Home,
                screen = Screen.Home.route
            ),
            NavigationItem(
                title = "Favorite",
                icon = Icons.Default.Favorite,
                screen = Screen.FavoriteScreen.route
            ),
            NavigationItem(
                title = "Profile",
                icon = Icons.Default.Person,
                screen = Screen.ProfileScreen.route
            )
        )

        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen,
                onClick = {
                    if (currentRoute != item.screen) { // ✅ Cegah navigasi duplikasi
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
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(item.title)
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = AppTheme.colorScheme.background,
                    selectedTextColor = AppTheme.colorScheme.primary,
                    unselectedIconColor = AppTheme.colorScheme.secondary,
                    unselectedTextColor = AppTheme.colorScheme.secondary,
                    selectedIndicatorColor = AppTheme.colorScheme.primary,
                    disabledIconColor = AppTheme.colorScheme.onBackground,
                    disabledTextColor = AppTheme.colorScheme.onBackground
                )
            )
        }
    }
}
