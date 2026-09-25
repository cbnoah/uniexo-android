package com.unicofrance.uniexo.utils

import androidx.compose.ui.graphics.Color

fun parseHexColor(colorString: String?, defaultColor: Color = Color.White): Color {
    if (colorString.isNullOrBlank()) return defaultColor
    val hex = colorString.removePrefix("#")
    val fullHex = if (hex.length == 6) "FF$hex" else hex
    val colorLong = fullHex.toLongOrNull(16) ?: return defaultColor
    return Color(colorLong)
}
