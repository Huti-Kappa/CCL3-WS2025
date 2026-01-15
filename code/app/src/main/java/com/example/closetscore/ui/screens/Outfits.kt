package com.example.closetscore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.TemplateCard
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.theme.Red
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodels.TemplateViewModel

@Composable
fun OutfitsScreen(
    navController: NavController,
    templateViewModel: TemplateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val templatesList by templateViewModel.templatesWithItems.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Template.route)
                },
                containerColor = Red,
                contentColor = White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create outfit")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (templatesList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No outfits yet. Press + to create one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Your Outfits",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(templatesList, span = { GridItemSpan(maxLineSpan) }) { templateWithItems ->
                        TemplateCard(
                            templateWithItems = templateWithItems,
                            onClick = {
                                navController.navigate("${Screen.TemplateDetail.route}/${templateWithItems.template.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}