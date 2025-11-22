package com.sustech.sus_community.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun MapContainer(
    modifier: Modifier
) {
    val munich = LatLng(48.1351, 11.5820)
    val munichMarkerState = MarkerState(position = munich)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(munich, 10f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = munichMarkerState,
            title = "Munich",
            snippet = "Marker in Munich"
        )
    }
}
