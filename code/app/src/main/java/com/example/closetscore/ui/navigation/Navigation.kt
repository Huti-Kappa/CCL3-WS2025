package com.example.closetscore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.closetscore.ui.screens.ClosetScreen
import com.example.closetscore.ui.screens.HomeScreen
import com.example.closetscore.ui.screens.ItemCreateScreen
import com.example.closetscore.ui.screens.ItemDetailScreen
import com.example.closetscore.ui.screens.OutfitsScreen
import com.example.closetscore.ui.screens.TemplateCreateScreen
import com.example.closetscore.ui.screens.TemplateDetailScreen

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
            HomeScreen(navController = navController)
        }

        composable(Screen.Closet.route) {
            ClosetScreen(navController = navController, "Closet / Wardrobe Screen")
        }

        composable(Screen.Outfits.route) {
            OutfitsScreen(navController = navController)
        }

        composable(Screen.Stats.route) {
            DummyScreen("Statistics Screen")
        }

        composable(Screen.Create.route) {
            ItemCreateScreen(navigateBack = { navController.popBackStack() })
        }

        composable(Screen.Template.route) {
            TemplateCreateScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = "${Screen.ItemDetail.route}/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                navigateBack = { navController.popBackStack() }
            )
        }


    }
}



@Composable
fun DummyScreen(text: String) {
    androidx.compose.material3.Text(text = text)
}