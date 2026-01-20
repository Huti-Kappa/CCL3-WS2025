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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.theme.*
import com.example.closetscore.ui.viewmodel.ScoreViewModel

@Composable
fun StatsScreen(navController: NavController, scoreViewModel: ScoreViewModel= viewModel(factory = AppViewModelProvider.Factory)) {
    val currentScore by scoreViewModel.score.collectAsState()
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
                StatsTag("142", "Items")
                StatsTag("32", "Total Wears")
                StatsTag("34%", "Thrifted")
                StatsTag("142", "Items")
            }
        }
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

@Composable
fun StatsTag(count: String, label: String) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 2.dp,
                horizontal = 16.dp,
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Checkroom,
                contentDescription = "Icon",
                modifier = Modifier.size(32.dp),
                tint = Color.DarkGray
            )

            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = count,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}