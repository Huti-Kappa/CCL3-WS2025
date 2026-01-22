package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.data.Item
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.MidTitle
import com.example.closetscore.ui.components.Score
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.viewmodel.ScoreViewModel

@Composable
fun HomeScreen(navController: NavController, itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory), scoreViewModel: ScoreViewModel= viewModel(factory = AppViewModelProvider.Factory)) {
    val itemsList by itemViewModel.repository.items.collectAsState(initial = emptyList())
    val currentScore by scoreViewModel.score.collectAsState()
    ItemGrid(navController, itemsList, currentScore)
}

@Composable
fun ItemGrid(navController: NavController, itemsList: List<Item>, currentScore: Int) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        val mostWornItems = itemsList.sortedByDescending { it.wearCount }.take(2)
        val leastWornItems = itemsList.sortedBy { it.wearCount }.take(2)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Score()
                Spacer(Modifier.padding(4.dp))
                MidTitle("Your Most Worn",
                    onViewAllClick = {
                        navController.navigate(Screen.Closet.route)
                    })
            }
        }
        items(mostWornItems) { item ->
            ItemCard(item = item,
                onClick = {navController.navigate("${Screen.ItemDetail.route}/${item.id}")})
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MidTitle("Your Least Worn",
                onViewAllClick = {
                    navController.navigate(Screen.Closet.route)
                })
        }
        items(leastWornItems) { item ->
            ItemCard(item = item,
                onClick = {navController.navigate("${Screen.ItemDetail.route}/${item.id}")})
        }
    }
}
