package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController
import com.example.closetscore.data.Item
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.Score
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.viewmodel.ItemViewModel

@Composable
fun HomeScreen(navController: NavController, itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val itemsList by itemViewModel.repository.items.collectAsState(initial = emptyList())
    ItemGrid(navController, itemsList)
}

@Composable
fun ItemGrid(navController: NavController, itemsList: List<Item>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text="Hello User",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Score()
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MidTitle("Your MVPs")
        }
        items(itemsList.take(2)) { item ->
            ItemCard(item = item,
                onClick = {navController.navigate("${Screen.ItemDetail.route}/${item.id}")})
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MidTitle("Closet Orphans")
        }
        items(itemsList.take(2)) { item ->
            ItemCard(item = item,
                onClick = {navController.navigate("${Screen.ItemDetail.route}/${item.id}")})
        }
    }
}


@Composable
fun MidTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title
        )
        Text(
            text = ""
        )
    }
}