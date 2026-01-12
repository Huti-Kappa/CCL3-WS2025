package com.example.closetscore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.closetscore.ui.screens.ClosetScreen
import com.example.closetscore.ui.screens.HomeScreen
import com.example.closetscore.ui.screens.ItemCreateScreen

@Composable
fun Navigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ){
        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Closet.route) {
            ClosetScreen("Closet / Wardrobe Screen")
        }

        composable(Screen.Outfits.route) {
            DummyScreen("Outfits Screen")
        }

        composable(Screen.Stats.route) {
            DummyScreen("Statistics Screen")
        }

        composable(Screen.Create.route) {
            ItemCreateScreen(navigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun DummyScreen(text: String) {
    androidx.compose.material3.Text(text = text)
}