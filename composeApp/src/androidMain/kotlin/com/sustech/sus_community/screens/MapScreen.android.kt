package com.sustech.sus_community.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.runtime.remember

@Composable
actual fun MapContainer(
    modifier: Modifier
) {
    val munich = LatLng(48.1351, 11.5820)
    val munichMarkerState = MarkerState(position = munich)

    // 1. SET ANGLE: Use Builder to add tilt and bearing
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(munich)
            .zoom(12f)      // Zoomed in slightly so tilt is more visible
            .tilt(45f)      // 0.0 - 90.0 (The viewing angle/pitch)
            .bearing(0f)    // 0 - 360 (Orientation: 90 is East, etc.)
            .build()
    }

    // 2. REMOVE UI: Configure MapUiSettings
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false, // Hides +/- buttons
            compassEnabled = false,      // Hides compass top-left
            mapToolbarEnabled = false,   // Hides the "Navigate/Open Maps" buttons on marker click
            myLocationButtonEnabled = false // Hides the "Locate Me" button
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings // <--- Apply settings here
    ) {
        Marker(
            state = munichMarkerState,
            title = "Munich",
            snippet = "Marker in Munich"
        )
    }
}