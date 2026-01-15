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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.MaterialType
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.AddItemGrid
import com.example.closetscore.ui.components.BasicInputField
import com.example.closetscore.ui.components.CategorySelection
import com.example.closetscore.ui.components.DatePickerField
import com.example.closetscore.ui.components.ImageSelector
import com.example.closetscore.ui.components.NameSection
import com.example.closetscore.ui.components.StepperRow
import com.example.closetscore.ui.components.SuccessView
import com.example.closetscore.ui.components.SwitchRow
import com.example.closetscore.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditItemScreen(
    itemId: Int,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
) {
    val formState by itemViewModel.formState.collectAsState()

    LaunchedEffect(itemId) {
        itemViewModel.initializeForm(itemId)
    }

    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }

    // Wait until form is loaded (id will be set when item is loaded)
    if (formState.id == 0 && itemId != 0) {
        // Show loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
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
            SuccessView()
        } else {
            AddItemGrid(
                itemViewModel = itemViewModel,
                onSuccess = { isSuccess = true },
                true
            )
        }
    }
}