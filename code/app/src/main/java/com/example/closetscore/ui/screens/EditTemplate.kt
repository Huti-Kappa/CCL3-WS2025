package com.example.closetscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.closetscore.db.TemplateEntity
import com.example.closetscore.db.TemplateWithItems
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.BasicInputField
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.viewmodels.TemplateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditTemplateScreen(
    templateId: Int,
    navigateBack: () -> Unit,
    templateViewModel: TemplateViewModel = viewModel(factory = AppViewModelProvider.Factory),
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var template by remember { mutableStateOf<TemplateWithItems?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var selectedItemIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isSuccess by remember { mutableStateOf(false) }

    val itemsList by itemViewModel.repository.items.collectAsState(initial = emptyList())

    LaunchedEffect(templateId) {
        withContext(Dispatchers.IO) {
            val loadedTemplate = templateViewModel.repository.getTemplateWithItems(templateId)
            template = loadedTemplate

            if (loadedTemplate != null) {
                name = loadedTemplate.template.name
                selectedItemIds = loadedTemplate.items.map { it.id }.toSet()
            }

            isLoading = false
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Edit Outfit",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        } else {
            AnimatedContent(
                targetState = isSuccess,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(500)) + scaleIn()) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "SuccessAnimation",
                modifier = Modifier.padding(paddingValues)
            ) { success ->
                if (success) {
                    EditTemplateSuccessView()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            BasicInputField(
                                label = "Template Name",
                                value = name,
                                onValueChange = { name = it }
                            )
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Select Items for Template",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }

                        items(itemsList) { item ->
                            SelectableItemCard(
                                item = item,
                                isSelected = selectedItemIds.contains(item.id),
                                onToggleSelection = { itemId ->
                                    selectedItemIds = if (selectedItemIds.contains(itemId)) {
                                        selectedItemIds - itemId
                                    } else {
                                        selectedItemIds + itemId
                                    }
                                }
                            )
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                enabled = name.isNotBlank() && selectedItemIds.isNotEmpty(),
                                onClick = {
                                    if (template != null) {
                                        val updatedTemplate = TemplateEntity(
                                            id = templateId,
                                            name = name,
                                            date = template!!.template.date,
                                            wearCount = template!!.template.wearCount,
                                            status = template!!.template.status
                                        )
                                        templateViewModel.updateTemplate(updatedTemplate)

                                        // Note: Logic to update items might need transaction or helper in Repo
                                        // This naive approach removes all and re-adds all
                                        template!!.items.forEach { item ->
                                            templateViewModel.removeItemFromTemplate(templateId, item.id)
                                        }

                                        selectedItemIds.forEach { itemId ->
                                            templateViewModel.addItemToTemplate(templateId, itemId)
                                        }

                                        isSuccess = true
                                    }
                                }
                            ) {
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableItemCardEdit(
    item: com.example.closetscore.db.ItemEntity,
    isSelected: Boolean,
    onToggleSelection: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 4.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onToggleSelection(item.id) }
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surface
            )
    ) {
        if (item.photoUri != null) {
            AsyncImage(
                model = item.photoUri,
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun EditTemplateSuccessView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = "Template Updated!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}