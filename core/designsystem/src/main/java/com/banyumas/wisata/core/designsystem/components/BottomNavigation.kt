package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme

@Composable
fun WbNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        content = content,
        containerColor = WBNavigationDefaults.navigationBarContainerColor()
    )
}

@Composable
fun RowScope.WbNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        icon = if (selected) selectedIcon else icon,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = WBNavigationDefaults.navigationSelectedItemColor(),
            selectedTextColor = WBNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = WBNavigationDefaults.navigationContentColor(),
            unselectedTextColor = WBNavigationDefaults.navigationContentColor(),
            indicatorColor = WBNavigationDefaults.navigationIndicatorColor()
        )
    )
}


object WBNavigationDefaults {
    /** Warna latar belakang untuk NavigationBar. Netral dan halus. */
    @Composable
    fun navigationBarContainerColor() = MaterialTheme.colorScheme.surfaceContainer

    /** Warna untuk ikon & teks pada item yang TIDAK TERPILIH. Penekanan medium. */
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    /** Warna untuk ikon & teks pada item yang TERPILIH. Penekanan tinggi. */
    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    /** Warna untuk latar belakang 'pil' indikator pada item yang TERPILIH. */
    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

@Preview(showBackground = true)
@Composable
fun PreviewWbNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        NavItem("Dashboard", Icons.Default.Dashboard),
        NavItem("Bookmarks", Icons.Default.Bookmark),
        NavItem("Profile", Icons.Default.Person)
    )

    WisataBanyumasTheme(dynamicColor = false) {
        WbNavigationBar(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                WbNavigationBarItem(
                    selected = selectedItem == index,
                    onClick = { selectedItem = index },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(text = item.label)
                    }
                )
            }
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)