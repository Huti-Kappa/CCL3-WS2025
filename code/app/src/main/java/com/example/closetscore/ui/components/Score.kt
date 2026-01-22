package com.example.closetscore.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.navigation.Screen
import com.example.closetscore.ui.theme.ChartBlue
import com.example.closetscore.ui.theme.ChartRed
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import kotlin.math.roundToInt

@Composable
fun Score(
    viewModel: ScoreViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val score by viewModel.score.collectAsState()
    val stats by viewModel.dataState.collectAsState()

    val thriftCount = (stats.itemSize * (stats.thriftAverage / 100)).roundToInt()
    val savedCo2 = (thriftCount * 15.0).roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { navController.navigate(Screen.Stats.route) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularScoreIndicator(
                score = score,
                color = MaterialTheme.colorScheme.onSecondary,
                textColor = MaterialTheme.colorScheme.onSecondary,
                trackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f),
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (score > 70) "Great progress!" else "Keep going!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "You've saved ~$savedCo2 kg CO₂",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        value = thriftCount.toString(),
                        label = "THRIFTED",
                        underlineColor = MaterialTheme.colorScheme.onSecondary,
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        labelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                    StatItem(
                        value = stats.itemSize.toString(),
                        label = "ITEMS",
                        underlineColor = ChartBlue,
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        labelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                    StatItem(
                        value = stats.totalWears.toString(),
                        label = "WEARS",
                        underlineColor = ChartRed,
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        labelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    color: Color,
    textColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )

            val sweepAngle = 360f * (score / 100f)

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-2).dp)
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 36.sp
                )
            )
            Text(
                text = "ECO SCORE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    underlineColor: Color,
    textColor: Color,
    labelColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = labelColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(24.dp)
                .height(4.dp)
                .background(underlineColor, RoundedCornerShape(50))
        )
    }
}