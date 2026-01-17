package com.example.closetscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.db.TemplateWithItems
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.SuccessView
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.theme.Black
import com.example.closetscore.ui.theme.Red
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.viewmodels.TemplateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@Composable
fun TemplateDetailScreen(
    navController: NavController,
    templateId: Int,
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    templateViewModel: TemplateViewModel = viewModel(factory = AppViewModelProvider.Factory),
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }

    AnimatedContent(
        targetState = isSuccess,
        transitionSpec = {
            (fadeIn(tween(500)) + scaleIn()) togetherWith fadeOut(tween(300))
        },
        label = "SuccessAnimation"
    ) { success ->
        if (success) {
            SuccessView(SuccessText = "Deleted Template!")
        } else {
            TemplateDetailComponents(
                navController = navController,
                templateId = templateId,
                templateViewModel = templateViewModel,
                navigateBack = navigateBack,
                navigateToEdit = navigateToEdit,
                itemViewModel = itemViewModel,
                onSuccessChange = { isSuccess = true }
            )
        }
    }
}

@Composable
fun TemplateDetailComponents(
    navController: NavController,
    templateId: Int,
    templateViewModel: TemplateViewModel,
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    itemViewModel: ItemViewModel,
    onSuccessChange: () -> Unit,
) {
    var templateWithItems by remember { mutableStateOf<TemplateWithItems?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(templateId) {
        withContext(Dispatchers.IO) {
            templateWithItems =
                templateViewModel.repository.getTemplateWithItems(templateId)
            isLoading = false
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
                    text = "Outfit Detail",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        } else {
            templateWithItems?.let { template ->

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    items(template.items) { item ->
                        ItemCard(
                            item = com.example.closetscore.data.Item(
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
                            ),
                            onClick = {
                                navController.navigate(
                                    "${Screen.ItemDetail.route}/${item.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
