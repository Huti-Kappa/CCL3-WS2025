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
import com.example.closetscore.ui.theme.Green


@Composable
fun Score() {
    val gradientStart = Color(0xFFE0F7FA) // Sehr helles Cyan/Grün
    val gradientEnd = Color(0xFFE8F5E9)   // Sehr helles Grün
    val scoreGreen = Color(0xFF22C55E)    // Das Haupt-Grün
    val textDarkGreen = Color(0xFF1B5E20) // Dunkles Grün für Titel

    // Daten (könnten später aus dem ViewModel kommen)
    val score = 78
    val savedCo2 = 12.4
    val thriftedCount = 23
    val totalItems = 47
    val wornPercentage = 89

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Außenabstand
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Hintergrund mit sanftem Verlauf
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientStart, gradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(20.dp) // Innenabstand
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LINKER TEIL: Der Kreis
                CircularScoreIndicator(
                    score = score,
                    color = scoreGreen,
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                // RECHTER TEIL: Texte und Stats
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Header: "Great progress!" + Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Great progress!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textDarkGreen,
                                fontSize = 18.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Kleines Blatt Icon
                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = null,
                        tint = Color(0xFF66BB6A),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // CO2 Text
                    Text(
                        text = "You've saved $savedCo2 kg CO₂",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Die 3 Statistiken unten
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            value = thriftedCount.toString(),
                            label = "THRIFTED",
                            underlineColor = scoreGreen
                        )
                        StatItem(
                            value = totalItems.toString(),
                            label = "ITEMS",
                            underlineColor = Color(0xFF3B82F6) // Blau
                        )
                        StatItem(
                            value = "$wornPercentage%",
                            label = "WORN",
                            underlineColor = Color(0xFFEC4899) // Pink
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// Helper 1: Der Kreis mit dem Score
// ---------------------------------------------------------
@Composable
fun CircularScoreIndicator(
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Der gezeichnete Kreis (Canvas)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 1. Hintergrund-Kreis (Grau)
            drawArc(
                color = Color.White.copy(alpha = 0.6f), // Weißer/Grauer Hintergrundring
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx()) // Dicke des Rings
            )

            // 2. Fortschritts-Kreis (Grün)
            // 360 * (Score / 100) berechnet den Winkel
            val sweepAngle = 360f * (score / 100f)

            drawArc(
                color = color,
                startAngle = -90f, // Startet oben bei 12 Uhr
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round) // Abgerundete Enden
            )
        }

        // Der Text in der Mitte
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-4).dp) // Leicht nach oben korrigiert für Optik
        ) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif, // Serif Schriftart wie im Bild
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20), // Dunkelgrün
                    fontSize = 38.sp
                )
            )
            Text(
                text = "ECO SCORE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )
        }
    }
}

// ---------------------------------------------------------
// Helper 2: Ein einzelnes Statistik-Item (Zahl + Label + Strich)
// ---------------------------------------------------------
@Composable
fun StatItem(
    value: String,
    label: String,
    underlineColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Der farbige Strich unten
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .background(underlineColor, RoundedCornerShape(2.dp))
        )
    }
}