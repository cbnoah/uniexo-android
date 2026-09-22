package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties

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

    GoogleMap(
        modifier = modifier,
        properties = mapProperties,
        contentPadding = PaddingValues(top = 20.dp)
    )
}