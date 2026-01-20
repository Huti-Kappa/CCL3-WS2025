package com.example.closetscore.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.components.SectionContainer
import com.example.closetscore.ui.components.StatsTag
import com.example.closetscore.ui.theme.*
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun StatsScreen(navController: NavController, scoreViewModel: ScoreViewModel= viewModel(factory = AppViewModelProvider.Factory)
                ) {
    val currentScore by scoreViewModel.score.collectAsState()
    val dataState by scoreViewModel.dataState.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
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
                StatsTag(dataState.itemSize.toString(), "Items")
                StatsTag(dataState.totalWears.toString(), "Total Wears")
                StatsTag(dataState.thriftAverage.toString() + "%", "Thrifted")
                StatsTag(dataState.priceAverage.toString()+ "€", "Average Price")
            }
        }
        Spacer(Modifier.padding(12.dp))
        Chart1()

    }
}

@Composable
fun Chart1(){
    SectionContainer{
        PieChart(
            data = listOf(
                Pie(label = "Tops", data = 40.0, color = Color(0xFF22C55E)),
                Pie(label = "Bottoms", data = 30.0, color = Color(0xFF3B82F6)),
                Pie(label = "Shoes", data = 20.0, color = Color(0xFFEC4899)),
                Pie(label = "Other", data = 10.0, color = Color(0xFFEAB308)),
            ),
            style = Pie.Style.Stroke(width = 30.dp), // <--- CREATES THE DONUT HOLE
            spaceDegree = 0f, // Set to higher if you want gaps between slices
            modifier = Modifier.size(200.dp)
        )
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

