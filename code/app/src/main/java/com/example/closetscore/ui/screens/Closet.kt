package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.SimpleSearchBar
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.viewmodel.ItemViewModel

enum class SortOption {
    DEFAULT,
    WEAR_COUNT_HIGH,
    WEAR_COUNT_LOW
}

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

    var currentSort by remember { mutableStateOf(SortOption.DEFAULT) }
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

    val isSearching = searchState.text.isNotEmpty()
    val baseList = if (isSearching) searchResults else itemsList

    val categoryFilteredList = remember(baseList, selectedCategory) {
        if (selectedCategory != null) {
            baseList.filter { it.category == selectedCategory }
        } else {
            baseList
        }
    }

    val itemsDisplay = remember(categoryFilteredList, currentSort) {
        when (currentSort) {
            SortOption.WEAR_COUNT_HIGH -> categoryFilteredList.sortedByDescending { it.wearCount }
            SortOption.WEAR_COUNT_LOW -> categoryFilteredList.sortedBy { it.wearCount }
            SortOption.DEFAULT -> categoryFilteredList
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SimpleSearchBar(
                        textFieldState = searchState,
                        onSearch = { query -> itemViewModel.searchItems(query) },
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Sort Options"
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Default") },
                                onClick = {
                                    currentSort = SortOption.DEFAULT
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Most Worn First") },
                                onClick = {
                                    currentSort = SortOption.WEAR_COUNT_HIGH
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Least Worn First") },
                                onClick = {
                                    currentSort = SortOption.WEAR_COUNT_LOW
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    val isTopSelected = selectedCategory == ItemCategory.Top

                    Button(
                        onClick = {
                            selectedCategory = if (isTopSelected) null else ItemCategory.Top
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTopSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,

                            contentColor = if (isTopSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(text = "Tops")
                    }
                }
            }

            items(
                items = itemsDisplay,
                key = { it.id }
            ) { item ->
                ItemCard(
                    item = item,
                    onClick = { navController.navigate("${Screen.ItemDetail.route}/${item.id}") }
                )
            }
        }
    }
}