package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.unicofrance.uniexo.R
import com.unicofrance.uniexo.ui.MainActivity
import com.unicofrance.uniexo.ui.detail.MarkerInfo
import com.unicofrance.uniexo.ui.lib.SvgIcon
import com.unicofrance.uniexo.utils.bitmapDescriptorFromVector
import kotlinx.coroutines.launch

@SuppressLint("PotentialBehaviorOverride")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleMapViewModel,
    onNavigationToDetail: (String) -> Unit
) {
    val context: Context = LocalContext.current

    // Marker Popup variables

    val showMarkerInfo = remember { mutableStateOf(false) }

    val containerAtSamePosition by viewModel.containersAtSamePosition.collectAsStateWithLifecycle()

    // User permission and location variables

    val permission by viewModel.permissions.collectAsState()

    val mapProperties = MapProperties(
        isMyLocationEnabled = permission.hasLocationPermissions,
    )

    val location by viewModel.location.collectAsStateWithLifecycle()

    // Lifecycle variables

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Permissions launcher

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(results)
        viewModel.getUserPosition(context)
    }

    // Camera position variables

    val defaultLocation = LatLng(46.506111, 2.457778)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            location ?: defaultLocation,
            if (location != null) 15f else 6f
        )
    }

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            if (viewModel.hasPermissions(context).hasLocationPermissions) {
                viewModel.getUserPosition(context)
            }
        }
        if (lifecycleState == Lifecycle.State.STARTED) {
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

    // Containers Info for ClusterManager

    val clusterItems by viewModel.clusterItems.collectAsStateWithLifecycle()

    var clusterManager by remember { mutableStateOf<ClusterManager<MarkerClusterItem>?>(null) }

    // Build
    Box(
        modifier = modifier
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            MapEffect(Unit) { map ->
                val manager = ClusterManager<MarkerClusterItem>(context, map).apply {
                    renderer =
                        object : DefaultClusterRenderer<MarkerClusterItem>(context, map, this) {
                            override fun onBeforeClusterItemRendered(
                                item: MarkerClusterItem,
                                markerOptions: MarkerOptions
                            ) {
                                pinBitmapDescriptor?.let { markerOptions.icon(it) }
                                super.onBeforeClusterItemRendered(item, markerOptions)
                            }
                        }.apply {
                            minClusterSize = 5
                        }
                    algorithm =
                        PreCachingAlgorithmDecorator(NonHierarchicalDistanceBasedAlgorithm())

                    setOnClusterClickListener { cluster ->
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    cluster.position,
                                    cameraPositionState.position.zoom + 2f
                                )
                            )
                        }
                        true
                    }

                    setOnClusterItemClickListener { item ->
                        if (item != null) {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        item.position,
                                        17f
                                    )
                                )
                            }
                            viewModel.containerAtSameLocation(
                                item.container.latitude,
                                item.container.longitude
                            )
                            showMarkerInfo.value = true
                        }
                        true
                    }
                }

                map.setOnCameraIdleListener(manager)
                map.setOnMarkerClickListener(manager)

                clusterManager = manager
            }

            LaunchedEffect(clusterItems, clusterManager) {
                clusterManager?.let { manager ->
                    manager.clearItems()
                    manager.addItems(clusterItems)
                    manager.cluster()
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

        SamePositionContainerList(showMarkerInfo, containerAtSamePosition, onNavigationToDetail)
    }
}