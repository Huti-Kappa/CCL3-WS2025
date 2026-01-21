package com.example.closetscore.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun CategoriesChart(viewModel: ScoreViewModel) {
    val pieData by viewModel.categoryStats.collectAsState()

    if (pieData.isEmpty()) return

    SectionContainer {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            PieChart(
                data = pieData,
                style = Pie.Style.Stroke(width = 25.dp),
                spaceDegree = 2f,
                modifier = Modifier.size(150.dp)
            )

            Spacer(Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                pieData.forEach { item ->
                    CategoryLegendItem(
                        color = item.color,
                        label = item.label,
                        percentage = item.data
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryLegendItem(color: Color, label: String?, percentage: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WearFrequencyChartFinal(viewModel: ScoreViewModel) {
    val barData by viewModel.wearStats.collectAsState()

    if (barData.isEmpty()) return
    val maxVal = barData.maxOfOrNull { it.values.firstOrNull()?.value ?: 0.0 } ?: 100.0
    val chartMax = if (maxVal == 0.0) 10.0 else maxVal

    val labelColor = MaterialTheme.colorScheme.onSurface

    SectionContainer {
        Text(
            text = "Wear Frequency",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(20.dp))
        ColumnChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 12.dp),

            data = barData,

            popupProperties = PopupProperties(enabled = false),
            labelHelperProperties = LabelHelperProperties(enabled = false),

            barProperties = BarProperties(
                spacing = 8.dp,
                thickness = 35.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 10.dp, topLeft = 10.dp)
            ),

            labelProperties = LabelProperties(
                enabled = true,
                padding = 8.dp,
                textStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            ),

            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = GridProperties.AxisProperties(thickness = 0.dp),
                yAxisProperties = GridProperties.AxisProperties(thickness = 0.dp),
            ),

            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            animationMode = AnimationMode.Together(delayBuilder = { it * 150L }),
            animationDelay = 200,
            maxValue = chartMax
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun TotalValueChart(scoreViewModel: ScoreViewModel) {
    val graphList = scoreViewModel.graphPoints.collectAsState().value

    if (graphList.isEmpty()) {
        return
    }
    val yValues = graphList.map { it.price }
    val xLabels = graphList.map { it.label }
    val maxDataValue = yValues.maxOrNull() ?: 1.0
    val chartMaxValue = maxDataValue * 1.2

    val chartColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface

    SectionContainer {
        Text(
            text = "Total Value History",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.padding(4.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            LineChart(
                data = listOf(
                    Line(
                        label = "Value",
                        values = yValues,
                        color = SolidColor(chartColor),
                        firstGradientFillColor = chartColor.copy(alpha = 0.5f),
                        secondGradientFillColor = Color.Transparent,
                        drawStyle = DrawStyle.Stroke(width = 4.dp),
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(chartColor),
                            strokeWidth = 3.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(chartColor)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                minValue = 0.0,
                maxValue = chartMaxValue,
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelsToShow = if (xLabels.size > 6) {
                    xLabels.filterIndexed { index, _ -> index % (xLabels.size / 5) == 0 }
                } else xLabels

                labelsToShow.forEach { label ->
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}