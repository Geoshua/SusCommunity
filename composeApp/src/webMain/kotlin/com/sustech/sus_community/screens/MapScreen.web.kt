package com.sustech.sus_community.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Web implementation of MapContainer.
 *
 * Displays sustainability locations in Munich as an interactive list
 * since Google Maps integration requires additional setup for web.
 */
@Composable
actual fun MapContainer(
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Sustainability Map - Munich",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Explore eco-friendly locations around the city",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Sustainability locations
        SustainabilityLocationCard(
            icon = Icons.Default.Recycling,
            title = "Recycling Center - Marienplatz",
            address = "Marienplatz, 80331 München",
            type = "Recycling",
            color = Color(0xFF4CAF50)
        )

        SustainabilityLocationCard(
            icon = Icons.Default.DirectionsBike,
            title = "MVG Bike Station - Olympiapark",
            address = "Olympiapark, 80809 München",
            type = "Bike Rental",
            color = Color(0xFF2196F3)
        )

        SustainabilityLocationCard(
            icon = Icons.Default.Store,
            title = "Organic Market - Viktualienmarkt",
            address = "Viktualienmarkt 3, 80331 München",
            type = "Sustainable Shop",
            color = Color(0xFFFF9800)
        )

        SustainabilityLocationCard(
            icon = Icons.Default.Event,
            title = "Community Garden - Schwabing",
            address = "Schwabing, 80802 München",
            type = "Community Event",
            color = Color(0xFF9C27B0)
        )
    }
}

@Composable
private fun SustainabilityLocationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    address: String,
    type: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = type,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color.copy(alpha = 0.1f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
