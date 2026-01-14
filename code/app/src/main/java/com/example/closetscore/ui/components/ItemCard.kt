package com.example.closetscore.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.closetscore.data.Item
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.theme.Black
import com.example.closetscore.ui.theme.DarkGrey
import com.example.closetscore.ui.theme.DarkestGrey
import com.example.closetscore.ui.theme.Green
import com.example.closetscore.ui.theme.Grey
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .combinedClickable(
                onClick = {
                    println("Clicked: ${item.name}")
                    onClick()
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDeleteDialog = true
                }
            ),
        border = BorderStroke(1.dp, Grey),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        // --- Image Section ---
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f) // Let image take available space
                .clip(RoundedCornerShape(12.dp))
                .background(White)
        ) {
            if (item.photoUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo of ${item.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Green.copy(alpha = 0.2f)), // Softer placeholder
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Image", color = DarkGrey, fontSize = 12.sp)
                }
            }
        }

        // --- Info Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                StyledTextCard(item)
            }

            // "Add Wear" Button
            FilledIconButton(
                onClick = { itemViewModel.incrementWearCount(item.id) },
                modifier = Modifier.size(42.dp), // Slightly smaller for better proportion
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Green,
                    contentColor = White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Wear",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // --- Delete Dialog ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to delete '${item.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // MAP UI ITEM -> DB ENTITY FOR DELETION
                        val ent = ItemEntity(
                            id = item.id,
                            name = item.name,
                            photoUri = item.photoUri,
                            brandName = item.brandName, // Updated
                            brandType = item.brandType, // New
                            material = item.material,   // New
                            category = item.category,
                            price = item.price,
                            isSecondHand = item.isSecondHand,
                            wearCount = item.wearCount,
                            dateAcquired = item.dateAcquired, // Updated
                            status = item.status
                            // removed store
                        )
                        itemViewModel.deleteItem(ent)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StyledTextCard(item: Item) {
    Column {
        // Item Name
        Text(
            text = item.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Black
        )

        // Brand Name (If exists)
        if (!item.brandName.isNullOrEmpty()) {
            Text(
                text = item.brandName,
                fontSize = 12.sp,
                color = DarkGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Category & Wear Count
        Text(
            text = "${item.category} • ${item.wearCount} Wears",
            fontSize = 12.sp,
            color = DarkestGrey
        )
    }
}