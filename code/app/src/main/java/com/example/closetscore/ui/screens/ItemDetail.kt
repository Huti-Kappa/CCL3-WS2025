package com.example.closetscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.SuccessView
import com.example.closetscore.ui.theme.Black
import com.example.closetscore.ui.theme.Red
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ItemDetailScreen(
    itemId: Int,
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var item by remember { mutableStateOf<ItemEntity?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("Item Created!") }

    LaunchedEffect(itemId) {
        withContext(Dispatchers.IO) {
            item = itemViewModel.repository.getItemById(itemId)
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }

    AnimatedContent(
        targetState = isSuccess,
        transitionSpec = {
            (fadeIn(animationSpec = tween(500)) + scaleIn()) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "SuccessAnimation"
    ) { success ->
        if (success) {
            SuccessView(successMessage)
        } else {
            ItemDetailComponent(
                itemId = itemId,
                navigateBack = navigateBack,
                navigateToEdit = navigateToEdit,
                itemViewModel = itemViewModel,
                onSuccess = { message ->
                    successMessage = message
                    isSuccess = true
                }
            )
        }
    }
}

@Composable
fun ItemDetailComponent(
    itemId: Int,
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSuccess: (String) -> Unit
) {
    var item by remember { mutableStateOf<ItemEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        withContext(Dispatchers.IO) {
            item = itemViewModel.repository.getItemById(itemId)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Closet Detail",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        item?.let { currentItem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (currentItem.photoUri != null) {
                        AsyncImage(
                            model = currentItem.photoUri,
                            contentDescription = currentItem.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentItem.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Purchase Price",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PRICE PER WEAR",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "€%.2f".format(
                                    if (currentItem.wearCount > 0)
                                        currentItem.price / currentItem.wearCount
                                    else
                                        currentItem.price
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TIMES WORN",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentItem.wearCount.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        itemViewModel.incrementWearCount(itemId)
                        coroutineScope.launch(Dispatchers.IO) {
                            item = itemViewModel.repository.getItemById(itemId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I'm wearing this today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = if (currentItem.wearCount > 0) {
                                val ppw = currentItem.price / currentItem.wearCount
                                val decrease =
                                    ((currentItem.price - ppw) / currentItem.price * 100).toInt()
                                "PPW decreased by $decrease% this month"
                            } else {
                                "Start wearing this item to track insights"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoSection(currentItem)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = navigateToEdit,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }



                Spacer(modifier = Modifier.height(24.dp))
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Item?") },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("What happened to this item?")
                            Spacer(modifier = Modifier.height(8.dp))
                            fun updateStatus(newStatus: ItemStatus, successText: String) {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val updatedItem = currentItem.copy(status = newStatus)
                                        itemViewModel.repository.updateItem(updatedItem)
                                    }
                                    showDeleteDialog = false
                                    onSuccess(successText)
                                }
                            }

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { updateStatus(ItemStatus.SOLD, "Marked as Sold!") }
                            ) {
                                Text("Sold", color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                            }

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { updateStatus(ItemStatus.DONATED, "Marked as Donated!") }
                            ) {
                                Text("Donated", color = androidx.compose.ui.graphics.Color(0xFF2196F3))
                            }

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { updateStatus(ItemStatus.TRASHED, "Marked as Trashed") }
                            ) {
                                Text("Trashed", color = androidx.compose.ui.graphics.Color.Gray)
                            }

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { updateStatus(ItemStatus.LOST, "Marked as Lost") }
                            ) {
                                Text("Lost", color = androidx.compose.ui.graphics.Color.Red)
                            }

                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            itemViewModel.repository.deleteItem(currentItem)
                                        }
                                        showDeleteDialog = false
                                        onSuccess("Item Permanently Deleted!")
                                    }
                                }
                            ) {
                                Text("Full Delete (Remove from Score)", color = androidx.compose.ui.graphics.Color.Red)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }
    }
}

fun ItemViewModel.incrementWearCount(itemId: Int) {}

@Composable
fun InfoSection(item: ItemEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            InfoRow("Brand", item.brandName ?: "Unknown")
            InfoRow("Impact", when(item.brandType) {
                BrandType.ECO_SUSTAINABLE -> "Eco / Ethical"
                BrandType.FAST_FASHION -> "Fast Fashion"
                BrandType.STANDARD -> "Standard"
            })
            InfoRow("Material", item.material.name.lowercase().replaceFirstChar { it.uppercase() })
            InfoRow("Category", item.category.name)
            InfoRow("Origin", if (item.isSecondHand) "Thrifted (Eco Bonus)" else "Bought New")
            InfoRow("Acquired", item.dateAcquired.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}