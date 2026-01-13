package com.example.closetscore.ui.components

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.closetscore.data.Item
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.theme.Green
import com.example.closetscore.ui.theme.Grey
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel

@OptIn(ExperimentalFoundationApi::class) // Notwendig für combinedClickable
@Composable
fun ItemCard(item: Item, onClick: () -> Unit, itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .combinedClickable(
                onClick = {
                    println("Clicked: ${item.name}") // Removed the comma here
                    onClick() // Call the lambda passed from Home.kt
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDeleteDialog = true
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Grey
        )
    )
    {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
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
                        .background(Green),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Image", color = White)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                StyledTextCard(item)
            }
            FilledIconButton(
                onClick = {
                    itemViewModel.incrementWearCount(item.id)
                },
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Green,
                    contentColor = Color.White
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
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to delete '${item.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        var ent = ItemEntity(
                            item.id,
                            item.name,
                            item.photoUri,
                            item.brand,
                            item.category,
                            item.price,
                            item.isSecondHand,
                            item.wearCount,
                            item.store,
                            item.date,
                            item.status,

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
fun StyledTextCard(itemEntity: Item) {
    Column() {
        Text(
            text = itemEntity.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp,
            color = Color.Black
        )
        Text(
            text = itemEntity.category.toString(),
            fontSize = 12.sp,
            lineHeight = 22.sp,
            color = Color.Gray
        )
        Text(
            text = itemEntity.wearCount.toString(),
            fontSize = 12.sp,
            lineHeight = 22.sp,
            color = Color.DarkGray
        )
    }
}