package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.unicofrance.uniexo.ui.MainActivity
import com.unicofrance.uniexo.ui.lib.SvgIcon


/**
 * Displays a button to activate the location permission
 * @param context: The context to use
 * @param launcher: The launcher to use to launch the permission request
 * @param modifier: The modifier to apply to the button
 */
@Composable
fun PositionActivationButton(
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier
            .padding(bottom = 25.dp)
            .scale(0.4f)
            .size(100.dp)
            .border(
                shape = RectangleShape, width = 1.dp,
                color = Color.Transparent
            ),
        containerColor = Color(0xAAFFFFFF),
        shape = RectangleShape,
        onClick = {
            if (!shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    ) {
        SvgIcon(
            url = "file:///android_asset/location_off.svg",
            modifier = Modifier.size(50.dp)
        )
    }
}