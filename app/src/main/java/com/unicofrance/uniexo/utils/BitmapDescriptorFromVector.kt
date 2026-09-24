package com.unicofrance.uniexo.utils

import android.content.Context
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    MapsInitializer.initialize(context)
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth / 2 else 1
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight / 2 else 1
    drawable.setBounds(0, 0, width, height)
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}