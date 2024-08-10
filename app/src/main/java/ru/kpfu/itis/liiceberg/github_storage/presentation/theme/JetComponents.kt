package ru.kpfu.itis.liiceberg.github_storage.presentation.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.BottomNavItems

@Composable
fun JetTopAppBar(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(600),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavItems.forEach { navItem ->

            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)
                },
                icon = {
                    var modifier = Modifier.size(24.dp)
                    if (navItem.rounded) {
                        modifier = modifier.clip(CircleShape)
                    }
                    Icon(
                        bitmap = ImageBitmap.imageResource(id = navItem.icon),
                        contentDescription = stringResource(id = navItem.label),
                        modifier = modifier,
                        tint = Color.Unspecified
                    )
                }
            )
        }
    }
}