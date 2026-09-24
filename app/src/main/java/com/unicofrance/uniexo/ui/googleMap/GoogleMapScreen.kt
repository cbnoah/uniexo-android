package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.unicofrance.uniexo.R
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.ui.MainActivity
import com.unicofrance.uniexo.ui.detail.MarkerInfo
import com.unicofrance.uniexo.ui.lib.SvgIcon

@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleMapViewModel,
    onNavigationToDetail: (String) -> Unit
) {
    val context: Context = LocalContext.current

    var showMarkerInfo by remember { mutableStateOf(false) }

    var markerInfoContainer by remember { mutableStateOf<Container?>(null) }

    val permission by viewModel.permissions.collectAsState()

    val location by viewModel.location.collectAsStateWithLifecycle()

    val containersLocation by viewModel.locations.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(results)
        viewModel.getUserPosition(context)
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
        } else {
            viewModel.getUserPosition(context)
        }
    }

    val defaultLocation = LatLng(46.506111, 2.457778)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            location ?: defaultLocation,
            if (location != null) 15f else 6f
        )
    }

    LaunchedEffect(location) {
        location?.let { userLocation ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
            )
        }
    }

    val pinBitmapDescriptor = remember(context) {
        bitmapDescriptorFromVector(context, R.drawable.map_pin)
    }

    Box(
        modifier = modifier
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            containersLocation.forEach { container ->
                key(container.id) {
                    val container = container
                    Marker(
                        title = container.label,
                        snippet = container.description,
                        state = rememberUpdatedMarkerState(
                            LatLng(
                                container.latitude,
                                container.longitude
                            )
                        ),
                        icon = pinBitmapDescriptor,
                        onClick = {
                            markerInfoContainer = container
                            showMarkerInfo = true
                            true
                        }
                    )
                }
            }
        }
        if (!permission.hasLocationPermissions) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 10.dp)
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
        if (showMarkerInfo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        showMarkerInfo = false
                        markerInfoContainer = null
                    }
            ) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    MarkerInfo(markerInfoContainer, onNavigationToDetail, modifier = Modifier)
                }
            }
        }
    }
}

private fun bitmapDescriptorFromVector(
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