package com.unicofrance.uniexo.ui.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unicofrance.uniexo.R

@Composable
fun BackButton(
    backToMap: (String) -> Unit,
    containerId: String? = null,
) {
    IconButton(
        modifier = Modifier.border(1.dp, Color.Gray, MaterialTheme.shapes.large),
        onClick = { backToMap(containerId ?: "") }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.cross),
            contentDescription = "Back to Map"
        )
    }
}