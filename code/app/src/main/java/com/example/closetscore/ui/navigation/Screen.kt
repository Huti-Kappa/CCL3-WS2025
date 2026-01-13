package com.example.closetscore.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screen (val route: String, val title: String, val icon: ImageVector){
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Closet : Screen("wardrobe", "Closet", Icons.Default.Checkroom)
    object Outfits : Screen("outfits", "Outfits", Icons.Default.Style)
    object Stats : Screen("stats", "Stats", Icons.Default.BarChart)
    object Create : Screen("create", "Create", Icons.Default.Person)
    object Template : Screen("template","Template", Icons.Default.Person)
}