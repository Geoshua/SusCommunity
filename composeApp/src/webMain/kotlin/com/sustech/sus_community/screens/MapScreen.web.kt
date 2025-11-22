package com.sustech.sus_community.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun MapContainer(
    modifier: Modifier
) {
    // Placeholder for Google Maps on Web
    // TODO: Implement Google Maps JavaScript API integration
    Box(
        modifier = modifier
            .background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Google Maps View (Web)",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Map integration pending",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Will show:",
                style = MaterialTheme.typography.bodySmall
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("• Recycling locations", style = MaterialTheme.typography.bodySmall)
                Text("• Bike rentals", style = MaterialTheme.typography.bodySmall)
                Text("• Sustainable shops", style = MaterialTheme.typography.bodySmall)
                Text("• Community events", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
