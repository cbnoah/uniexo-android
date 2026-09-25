package com.unicofrance.uniexo.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailInfosTextBox(label: String, content: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color.Gray, MaterialTheme.shapes.large)
            .background(Color(0xFFF4F4F4), MaterialTheme.shapes.large)
    ) {

        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(
                4.dp,
                Alignment.CenterVertically
            )
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Light,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
            Text(
                text = content ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF3A3B3F)
            )
        }
    }
}