package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.unicofrance.uniexo.ui.MainActivity
import com.unicofrance.uniexo.ui.lib.SvgIcon

@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleMapViewModel
) {
    val context: Context = LocalContext.current

    val permission by viewModel.permissions.collectAsState()

    val location by viewModel.location.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(results)
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = permission.hasLocationPermissions,
    )

    LaunchedEffect(Unit) {
        if (!viewModel.hasPermissions(context).hasLocationPermissions) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = modifier
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            contentPadding = PaddingValues(top = 20.dp)
        )

        if (!permission.hasLocationPermissions) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 10.dp)
                    .scale(0.4f)
                    .size(100.dp).border(
                        shape = RectangleShape, width = 1.dp,
                        color = Color.Transparent
                    ),
                containerColor = Color(0xAAFFFFFF),
                shape = RectangleShape,
                onClick = {
                    if (!shouldShowRequestPermissionRationale(context as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
    }
}