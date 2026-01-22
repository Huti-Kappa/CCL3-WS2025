package com.example.closetscore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeaderText(text: String){
    Text(
        text=text,
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.padding(bottom = 2.dp)
    )
}

@Composable
fun MidTitle(title: String, onViewAllClick: () -> Unit) { // 1. Add the click function here
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Your Title
        Text(
            text = title,
            // style = MaterialTheme.typography.titleMedium (Optional: makes it look like a header)
        )

        // The "View All" Button
        TextButton(onClick = onViewAllClick) {
            Text(text = "View All")
        }
    }
}