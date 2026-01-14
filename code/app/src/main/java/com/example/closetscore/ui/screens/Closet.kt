package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.navigation.Screen
import androidx.navigation.NavController
import com.example.closetscore.ui.components.HeaderText


@Composable
fun ClosetScreen(navController: NavController, text: String) {
    ClosetGrid(navController)
}


@Composable
fun ClosetGrid(navController: NavController, itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val itemsList by itemViewModel.repository.items.collectAsState(initial = emptyList())
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HeaderText("Your Closet")
        }
        items(itemsList) { item ->
            ItemCard(item = item,
                    onClick = {navController.navigate("${Screen.ItemDetail.route}/${item.id}")})
        }
}


@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search your clothes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}}