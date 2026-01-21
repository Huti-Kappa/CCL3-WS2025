package com.example.closetscore.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.theme.ChartBlue
import com.example.closetscore.ui.theme.ChartGreen
import com.example.closetscore.ui.theme.ChartRed
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import kotlin.math.roundToInt

@Composable
fun Score(viewModel: ScoreViewModel= viewModel(factory = AppViewModelProvider.Factory)) {
    val score by viewModel.score.collectAsState()
    val stats by viewModel.dataState.collectAsState()

    // Dynamic Colors based on Theme
    val gradientStart = MaterialTheme.colorScheme.surface
    val gradientEnd = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Logic Calculations based on ViewModel Data
    // Assuming approx 15kg CO2 saved per thrifted item
    val thriftCount = (stats.itemSize * (stats.thriftAverage / 100)).roundToInt()
    val savedCo2 = (thriftCount * 15.0).roundToInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientStart, gradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Score Circle
                CircularScoreIndicator(
                    score = score,
                    color = primaryColor,
                    textColor = primaryColor,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Right: Text and Stats
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (score > 70) "Great progress!" else "Keep going!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = 18.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "You've saved ~$savedCo2 kg CO₂",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = subTextColor,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            value = thriftCount.toString(),
                            label = "THRIFTED",
                            underlineColor = ChartGreen, // Uses your Chart Colors
                            textColor = textColor,
                            labelColor = subTextColor
                        )
                        StatItem(
                            value = stats.itemSize.toString(),
                            label = "ITEMS",
                            underlineColor = ChartBlue,
                            textColor = textColor,
                            labelColor = subTextColor
                        )
                        StatItem(
                            value = stats.totalWears.toString(),
                            label = "WEARS",
                            underlineColor = ChartRed, // Or BrandRed
                            textColor = textColor,
                            labelColor = subTextColor
                        )
                    }
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
            // Background Ring
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx())
            )

            // Progress Ring
            val sweepAngle = 360f * (score / 100f)

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-4).dp)
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 38.sp
                )
            )
            Text(
                text = "ECO SCORE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .background(underlineColor, RoundedCornerShape(2.dp))
        )
    }
}