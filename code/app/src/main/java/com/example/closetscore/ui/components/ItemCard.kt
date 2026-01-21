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
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.viewmodel.ItemViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onIncrementWear: () -> Unit = {},
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
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
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
            if (item.isSecondHand) {
                Text(
                    text = "Thrifted",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

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

            FilledIconButton(
                onClick = {
                    itemViewModel.incrementWearCount(item.id)
                    onIncrementWear()
                },
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
            text = { Text("This Item I have ...") },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,

            confirmButton = {
                fun confirmAction(newStatus: ItemStatus) {
                    val ent = ItemEntity(
                        id = item.id,
                        name = item.name,
                        photoUri = item.photoUri,
                        brandName = item.brandName,
                        brandType = item.brandType,
                        material = item.material,
                        category = item.category,
                        price = item.price,
                        isSecondHand = item.isSecondHand,
                        wearCount = item.wearCount,
                        dateAcquired = item.dateAcquired,
                        status = newStatus
                    )
                    itemViewModel.updateItem(ent)
                    showDeleteDialog = false
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { confirmAction(ItemStatus.SOLD) }
                    ) {
                        Text("Sold", color = Color(0xFF4CAF50))
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { confirmAction(ItemStatus.DONATED) }
                    ) {
                        Text("Donated", color = Color(0xFF2196F3))
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { confirmAction(ItemStatus.TRASHED) }
                    ) {
                        Text("Trashed", color = Color.Gray)
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { confirmAction(ItemStatus.LOST) }
                    ) {
                        Text("Lost", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = {
                        val ent = ItemEntity(
                            id = item.id,
                            name = item.name,
                            photoUri = item.photoUri,
                            brandName = item.brandName,
                            brandType = item.brandType,
                            material = item.material,
                            category = item.category,
                            price = item.price,
                            isSecondHand = item.isSecondHand,
                            wearCount = item.wearCount,
                            dateAcquired = item.dateAcquired,
                            status = item.status
                        )
                        itemViewModel.deleteItem(ent)
                        showDeleteDialog = false }
                    )
                    {
                        Text("Full Delete (Remove from Score)", color = MaterialTheme.colorScheme.error)
                    }

                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        )
    }
}

@Composable
fun StyledTextCard(item: Item) {
    Column {
        Text(
            text = item.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        val cpwValue = if (item.wearCount > 0) {
            item.price / item.wearCount
        } else {
            item.price
        }
        Text(
            text = "${item.wearCount} Wears",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "€%.2f".format(cpwValue),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}