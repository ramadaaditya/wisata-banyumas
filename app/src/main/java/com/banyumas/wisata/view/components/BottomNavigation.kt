package com.banyumas.wisata.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
    currentUserId: String // ✅ Pastikan userId diteruskan dengan benar
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
                screen = Screen.Home.createRoute(currentUserId) // ✅ Fix: Home dengan userId
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
            val isSelected = when {
                item.screen.startsWith("home") -> currentRoute?.startsWith(Screen.Home.ROUTE.split("/{")[0]) ?: false
                else -> currentRoute == item.screen
            }

            NavigationBarItem(
                selected = isSelected, // ✅ Sekarang membandingkan dengan path dinamis
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
                }
            )
        }
    }
}
