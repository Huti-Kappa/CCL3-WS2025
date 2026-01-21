package com.example.closetscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.TemplateWithItems
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.theme.Black
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.viewmodels.TemplateViewModel

@Composable
fun TemplateCard(
    templateWithItems: TemplateWithItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    templateViewModel: TemplateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = templateWithItems.template.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${templateWithItems.template.wearCount} Wears",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    templateWithItems.items.take(4).forEach { item ->
                        ItemThumbnail(item = item)
                    }
                }
            }

            FilledIconButton(
                onClick = {
                    templateViewModel.incrementTemplateWearCount(templateWithItems.template.id)
                    templateWithItems.items.forEach { item ->
                        itemViewModel.incrementWearCount(item.id)
                    }
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
}

@Composable
private fun ItemThumbnail(
    item: ItemEntity,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.onSecondary

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, Black.copy(alpha = 2f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (item.photoUri != null) {
            AsyncImage(
                model = item.photoUri,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = item.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = Black
            )
        }
    }
}