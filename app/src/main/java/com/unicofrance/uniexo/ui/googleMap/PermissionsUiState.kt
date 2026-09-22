package com.unicofrance.uniexo.ui.googleMap

data class PermissionsUiState(
    val hasFineLocation: Boolean = false,
    val hasCoarseLocation: Boolean = false
) {
    val hasLocationPermissions: Boolean
        get() = hasFineLocation || hasCoarseLocation
}