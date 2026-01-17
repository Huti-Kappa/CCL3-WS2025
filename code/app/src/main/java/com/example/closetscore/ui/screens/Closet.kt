package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.SimpleSearchBar
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.viewmodel.ItemViewModel

@Composable
fun ClosetScreen(navController: NavController, text: String) {
    val viewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ClosetGrid(navController, viewModel)
}

@Composable
fun ClosetGrid(
    navController: NavController,
    itemViewModel: ItemViewModel
) {
    val searchState = remember { TextFieldState() }
    val searchResults by itemViewModel.searchResults.collectAsState()
    val itemsList by itemViewModel.repository.items.collectAsState(initial = emptyList())

    val isSearching = searchState.text.isNotEmpty()
    val itemsDisplay = if (isSearching) searchResults else itemsList

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleSearchBar(
            textFieldState = searchState,
            onSearch = { query -> itemViewModel.searchItems(query) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HeaderText(if (isSearching) "Search Results" else "Your Closet")
            }

            items(itemsDisplay) { item ->
                ItemCard(
                    item = item,
                    onClick = { navController.navigate("${Screen.ItemDetail.route}/${item.id}") }
                )
            }
        }
    }
}