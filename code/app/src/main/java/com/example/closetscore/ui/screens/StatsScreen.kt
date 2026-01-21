package com.example.closetscore.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.components.StatsTag
import com.example.closetscore.ui.theme.*
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import ir.ehsannarmani.compose_charts.extensions.format
import androidx.compose.material3.Text
import com.example.closetscore.ui.components.CategoriesChart
import com.example.closetscore.ui.components.TotalValueChart
import com.example.closetscore.ui.components.WearFrequencyChartFinal

@Composable
fun StatsScreen(scoreViewModel: ScoreViewModel= viewModel(factory = AppViewModelProvider.Factory)) {
    val currentScore by scoreViewModel.score.collectAsState()
    val dataState by scoreViewModel.dataState.collectAsState()
    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        HeaderText("Dashboard")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainScore(currentScore)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsTag(
                    count = dataState.itemSize.toString(),
                    label = "Items",
                    icon = Icons.Default.Checkroom
                )
                StatsTag(
                    count = dataState.totalWears.toString(),
                    label = "Total Wears",
                    icon = Icons.Default.Repeat
                )
                StatsTag(
                    count = dataState.thriftAverage.format(2) + "%",
                    label = "Thrifted",
                    icon = Icons.Default.Eco
                )
                StatsTag(
                    count = dataState.priceAverage.format(2) + "€",
                    label = "Average Price",
                    icon = Icons.Default.EuroSymbol
                )
            }
        }
        Spacer(Modifier.padding(12.dp))
        TotalValueChart(scoreViewModel)
        Spacer(Modifier.padding(12.dp))
        WearFrequencyChartFinal(scoreViewModel)
        Spacer(Modifier.padding(12.dp))
        CategoriesChart(scoreViewModel)
        Spacer(Modifier.padding(12.dp))

    }
}

@Composable
fun MainScore(score: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
            .background(color = LightGreen, shape = CircleShape)
    ) {
        Text(
            text = score.toString(),
            color = White,
            fontSize = 110.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



