package com.example.closetscore.ui.screens
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.HeaderText
import com.example.closetscore.ui.components.SectionContainer
import com.example.closetscore.ui.components.StatsTag
import com.example.closetscore.ui.theme.*
import com.example.closetscore.ui.viewmodel.ScoreViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

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
                StatsTag(dataState.itemSize.toString(), "Items")
                StatsTag(dataState.totalWears.toString(), "Total Wears")
                StatsTag(dataState.thriftAverage.format(2).toString() + "%", "Thrifted")
                StatsTag(dataState.priceAverage.format(2).toString()+ "€", "Average Price")
            }
        }
        Spacer(Modifier.padding(12.dp))
        CostPerWearChart(scoreViewModel)
        Spacer(Modifier.padding(12.dp))
        WearFrequencyChartFinal()
        Spacer(Modifier.padding(12.dp))
        CategoriesChart()
        Spacer(Modifier.padding(12.dp))

    }
}
@Composable
fun CategoriesChart() {
    // Daten einmal definieren, damit wir sie für Chart UND Legende nutzen können
    val pieData = listOf(
        Pie(label = "Tops", data = 40.0, color = Color(0xFF22C55E)),
        Pie(label = "Bottoms", data = 30.0, color = Color(0xFF3B82F6)),
        Pie(label = "Shoes", data = 20.0, color = Color(0xFFEC4899)),
        Pie(label = "Other", data = 10.0, color = Color(0xFFEAB308)),
    )

    SectionContainer {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))

        // HIER: Row sorgt dafür, dass Chart und Legende NEBENEINANDER stehen
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Platz verteilen
        ) {

            // 1. Das Tortendiagramm (Links)
            // Wir geben ihm eine feste Größe, damit es nicht alles verdrängt
            PieChart(
                data = pieData,
                style = Pie.Style.Stroke(width = 25.dp), // Etwas dünner sieht oft eleganter aus
                spaceDegree = 2f, // Kleine Lücke zwischen den Stücken
                modifier = Modifier.size(150.dp) // Kompakte Größe für Side-by-Side
            )

            Spacer(Modifier.width(24.dp)) // Abstand zwischen Chart und Legende

            // 2. Die Legende (Rechts)
            // Column sorgt dafür, dass die Legendeneinträge untereinander stehen
            Column(
                modifier = Modifier.weight(1f) // Nimmt den restlichen Platz ein
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
        // Der farbige Punkt
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))

        // Text und Prozentzahl
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium, // oder bodySmall
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
@Composable
fun WearFrequencyChartFinal() {
    val colorRed = Color(0xFFFF9B9B)
    val colorYellow = Color(0xFFFFD750)
    val colorGreenLight = Color(0xFF6CE590)
    val colorGreen = Color(0xFF22C55E)

    val dummyData = remember {
        listOf(
            Bars(
                label = "0",
                values = listOf(
                    Bars.Data(value = 25.0, color = SolidColor(colorRed))
                )
            ),
            Bars(
                label = "1-5",
                values = listOf(
                    Bars.Data(value = 45.0, color = SolidColor(colorYellow))
                )
            ),
            Bars(
                label = "6-15",
                values = listOf(
                    Bars.Data(value = 100.0, color = SolidColor(colorGreenLight))
                )
            ),
            Bars(
                label = "16-30",
                values = listOf(
                    Bars.Data(value = 70.0, color = SolidColor(colorGreenLight))
                )
            ),
            Bars(
                label = "30+",
                values = listOf(
                    Bars.Data(value = 40.0, color = SolidColor(colorGreen))
                )
            )
        )
    }

    SectionContainer {
        Text(
            text = "Wear Frequency ",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        ColumnChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 12.dp),

            data = dummyData,
            barProperties = BarProperties(
                spacing = 8.dp,
                thickness = 35.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 10.dp, topLeft = 10.dp)
            ),

            labelProperties = LabelProperties(
                enabled = true,
                padding = 8.dp
            ),

            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = GridProperties.AxisProperties(thickness = 0.dp),
                yAxisProperties = GridProperties.AxisProperties(thickness = 0.dp),
            ),

            // Animation (Macht es lebendig)
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            animationMode = AnimationMode.Together(delayBuilder = { it * 150L }),
            animationDelay = 200,
            maxValue = 110.0
        )
    }
}


@Composable
fun CostPerWearChart(scoreViewModel: ScoreViewModel) {
    val graphList = scoreViewModel.graphPoints.collectAsState().value

    if (graphList.isEmpty()) {
        Text("Noch keine Daten verfügbar")
        return
    }
    val yValues = graphList.map { it.price }
    val xLabels = graphList.map { it.label }
    val maxDataValue = yValues.maxOrNull() ?: 1.0
    val chartMaxValue = maxDataValue * 1.2

    SectionContainer {
        Text(text = "Total Value History")
        Spacer(Modifier.padding(4.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            LineChart(
                data = listOf(
                    Line(
                        label = "Value",
                        values = yValues,
                        color = SolidColor(Color(0xFF22C55E)),
                        firstGradientFillColor = Color(0xFF22C55E).copy(alpha = 0.5f),
                        secondGradientFillColor = Color.Transparent,
                        drawStyle = DrawStyle.Stroke(width = 4.dp),
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color(0xFF22C55E)),
                            strokeWidth = 3.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(Color(0xFF22C55E))
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                xLabels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        color = Color.Gray
                    )
                }
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

