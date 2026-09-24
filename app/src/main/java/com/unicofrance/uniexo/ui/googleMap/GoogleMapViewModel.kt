package com.unicofrance.uniexo.ui.googleMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.data.repositories.ContainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoogleMapViewModel(
    private val containerRepository: ContainerRepository,
) : ViewModel() {
    private val _location = MutableStateFlow<LatLng?>(null)

    val location = _location.asStateFlow()

    private val _locations = MutableStateFlow<List<Container>>(listOf())

    val locations = _locations.asStateFlow()

    private val _clusterItems = MutableStateFlow<List<MarkerClusterItem>>(listOf())

    val clusterItems = _clusterItems.asStateFlow()

    init {
        viewModelScope.launch {
            containerRepository.getAll().collect { containers ->
                _locations.value = containers
                _clusterItems.value = containers.map { MarkerClusterItem(it) }
            }
        }
    }

    private val _permissions = MutableStateFlow(PermissionsUiState())
    val permissions = _permissions.asStateFlow()

    fun hasPermissions(context: Context): PermissionsUiState {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _permissions.update { currentState ->
            currentState.copy(
                hasFineLocation = fineLocationGranted,
                hasCoarseLocation = coarseLocationGranted
            )
        }

        return _permissions.value
    }

    fun onPermissionsResult(results: Map<String, Boolean>) {
        _permissions.update { currentState ->
            currentState.copy(
                hasFineLocation = results[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: currentState.hasFineLocation,
                hasCoarseLocation = results[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: currentState.hasCoarseLocation
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getUserPosition(context: Context) {
        if (!permissions.value.hasLocationPermissions) {
            return
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _location.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

}

class MarkerClusterItem(
    val container: Container,
) : ClusterItem {
    private val itemPosition = LatLng(container.latitude, container.longitude)
    private val itemTitle = container.label
    private val itemSnippet = container.description

    override fun getPosition(): LatLng = itemPosition
    override fun getTitle(): String = itemTitle
    override fun getSnippet(): String = itemSnippet
    override fun getZIndex(): Float = 1f

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MarkerClusterItem) return false
        return container.id == other.container.id
    }

    override fun hashCode(): Int {
        return container.id.hashCode()
    }
}