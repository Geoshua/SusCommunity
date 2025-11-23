package com.sustech.sus_community.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch


/**
 * Category types for map markers
 */
enum class MapCategory(
    val label: String,
    val icon: ImageVector,
    val color: Color
) {
    RECYCLING("Recycling", Icons.Default.Recycling, Color(0xFF4CAF50)),
    CLOTHES_RECYCLING("Clothes", Icons.Default.Checkroom, Color(0xFF8BC34A)),
    PET_SITTING("Pet Sitting", Icons.Default.Pets, Color(0xFFFF9800)),
    CHORES("Chores", Icons.Default.CleaningServices, Color(0xFFE91E63)),
    TUTORING("Tutoring", Icons.Default.School, Color(0xFF9C27B0)),
    ELDERLY_HELP("Elderly Help", Icons.Default.Elderly, Color(0xFF3F51B5)),
    MOVING_HELP("Moving", Icons.Default.LocalShipping, Color(0xFF00BCD4)),
    BIKE_RENTAL("Bike Rental", Icons.Default.DirectionsBike, Color(0xFF2196F3))
}

/**
 * Data class for map locations
 */
data class MapLocation(
    val id: String,
    val title: String,
    val description: String,
    val position: LatLng,
    val category: MapCategory,
    val author: String? = null,
    val isPost: Boolean = false // true for user posts, false for sustainability locations
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MapContainer(
    modifier: Modifier
) {
    val munich = LatLng(48.1351, 11.5820)

    // State for selected categories
    var selectedCategories by remember { mutableStateOf(MapCategory.values().toSet()) }

    // State for bottom sheet
    var sheetPeekHeight by remember { mutableStateOf(80.dp) }
    var isSheetExpanded by remember { mutableStateOf(false) }

    // State for highlighted location
    var highlightedLocationId by remember { mutableStateOf<String?>(null) }

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(munich)
            .zoom(12f)
            .tilt(45f)
            .bearing(0f)
            .build()
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false
        )
    }

    // Sample locations
    val locations = remember { createSampleLocations() }

    // Filter locations based on selected categories
    val filteredLocations = locations.filter { it.category in selectedCategories }

    Box(modifier = modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        ) {
            filteredLocations.forEach { location ->
                val isHighlighted = location.id == highlightedLocationId
                val markerColor = if (isHighlighted) {
                    BitmapDescriptorFactory.HUE_YELLOW
                } else {
                    when (location.category) {
                        MapCategory.RECYCLING, MapCategory.CLOTHES_RECYCLING -> BitmapDescriptorFactory.HUE_GREEN
                        MapCategory.PET_SITTING -> BitmapDescriptorFactory.HUE_ORANGE
                        MapCategory.CHORES, MapCategory.ELDERLY_HELP -> BitmapDescriptorFactory.HUE_RED
                        MapCategory.TUTORING -> BitmapDescriptorFactory.HUE_VIOLET
                        MapCategory.MOVING_HELP -> BitmapDescriptorFactory.HUE_CYAN
                        MapCategory.BIKE_RENTAL -> BitmapDescriptorFactory.HUE_BLUE
                    }
                }

                Marker(
                    state = MarkerState(position = location.position),
                    title = location.title,
                    snippet = location.description,
                    icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                    onClick = {
                        highlightedLocationId = location.id
                        isSheetExpanded = true
                        true
                    }
                )
            }
        }

        // Category filters at the top
        CategoryFilters(
            selectedCategories = selectedCategories,
            onCategoryToggle = { category ->
                selectedCategories = if (category in selectedCategories) {
                    selectedCategories - category
                } else {
                    selectedCategories + category
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )

        // Draggable bottom sheet
        DraggableBottomSheet(
            locations = filteredLocations,
            isExpanded = isSheetExpanded,
            onExpandedChange = { isSheetExpanded = it },
            highlightedLocationId = highlightedLocationId,
            onLocationClick = { location ->
                highlightedLocationId = location.id
                // Animate camera to location
                cameraPositionState.position = CameraPosition.Builder()
                    .target(location.position)
                    .zoom(15f)
                    .tilt(45f)
                    .bearing(0f)
                    .build()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CategoryFilters(
    selectedCategories: Set<MapCategory>,
    onCategoryToggle: (MapCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MapCategory.values()) { category ->
                val isSelected = category in selectedCategories
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryToggle(category) },
                    label = { Text(category.label, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.label,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.color.copy(alpha = 0.3f),
                        selectedLabelColor = category.color,
                        selectedLeadingIconColor = category.color
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraggableBottomSheet(
    locations: List<MapLocation>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    highlightedLocationId: String?,
    onLocationClick: (MapLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetHeight by animateDpAsState(
        targetValue = if (isExpanded) 500.dp else 80.dp,
        label = "sheetHeight"
    )

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to highlighted item
    LaunchedEffect(highlightedLocationId) {
        highlightedLocationId?.let { id ->
            val index = locations.indexOfFirst { it.id == id }
            if (index != -1) {
                coroutineScope.launch {
                    listState.animateScrollToItem(index)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .clickable { onExpandedChange(!isExpanded) },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {}
            }

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Locations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${locations.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { onExpandedChange(!isExpanded) }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            Divider()

            // Locations list
            if (isExpanded) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(locations) { location ->
                        LocationCard(
                            location = location,
                            isHighlighted = location.id == highlightedLocationId,
                            onClick = { onLocationClick(location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: MapLocation,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                location.category.color.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isHighlighted) {
            androidx.compose.foundation.BorderStroke(2.dp, location.category.color)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = location.category.color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = location.category.icon,
                        contentDescription = location.category.label,
                        tint = location.category.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = location.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = location.category.color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = location.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = location.category.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Post type badge
                    if (location.isPost) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Request",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Author
                    location.author?.let { author ->
                        Text(
                            text = "by $author",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates sample locations for the map
 */
private fun createSampleLocations(): List<MapLocation> {
    return listOf(
        // Sustainability locations
        MapLocation(
            id = "rec1",
            title = "Recycling Center - Marienplatz",
            description = "Glass, paper, and plastic recycling",
            position = LatLng(48.1374, 11.5755),
            category = MapCategory.RECYCLING,
            isPost = false
        ),
        MapLocation(
            id = "rec2",
            title = "Recycling Station - Sendling",
            description = "Multi-material recycling facility",
            position = LatLng(48.1166, 11.5600),
            category = MapCategory.RECYCLING,
            isPost = false
        ),
        MapLocation(
            id = "clothes1",
            title = "Clothes Donation - Schwabing",
            description = "Clothing and textile recycling",
            position = LatLng(48.1642, 11.5892),
            category = MapCategory.CLOTHES_RECYCLING,
            isPost = false
        ),
        MapLocation(
            id = "clothes2",
            title = "Second-Hand Store - Haidhausen",
            description = "Donate or buy used clothes",
            position = LatLng(48.1350, 11.5950),
            category = MapCategory.CLOTHES_RECYCLING,
            isPost = false
        ),
        MapLocation(
            id = "bike1",
            title = "MVG Bike Station - Olympiapark",
            description = "Bike rental and sharing",
            position = LatLng(48.1486, 11.5680),
            category = MapCategory.BIKE_RENTAL,
            isPost = false
        ),
        MapLocation(
            id = "bike2",
            title = "MVG Bike - Hauptbahnhof",
            description = "Central station bike rental",
            position = LatLng(48.1405, 11.5580),
            category = MapCategory.BIKE_RENTAL,
            isPost = false
        ),

        // User posts
        MapLocation(
            id = "post1",
            title = "Need pet sitter for 2 cats",
            description = "Looking for someone to feed my cats while I'm away for the weekend",
            position = LatLng(48.1450, 11.5750),
            category = MapCategory.PET_SITTING,
            author = "Anna M.",
            isPost = true
        ),
        MapLocation(
            id = "post2",
            title = "Dog walking needed",
            description = "Need daily dog walker for my golden retriever",
            position = LatLng(48.1280, 11.5650),
            category = MapCategory.PET_SITTING,
            author = "Thomas K.",
            isPost = true
        ),
        MapLocation(
            id = "post3",
            title = "Help with garden cleanup",
            description = "Need help cleaning up garden before winter",
            position = LatLng(48.1550, 11.5850),
            category = MapCategory.CHORES,
            author = "Maria S.",
            isPost = true
        ),
        MapLocation(
            id = "post4",
            title = "Window cleaning service",
            description = "Looking for help cleaning windows in apartment",
            position = LatLng(48.1200, 11.5700),
            category = MapCategory.CHORES,
            author = "Klaus W.",
            isPost = true
        ),
        MapLocation(
            id = "post5",
            title = "Math tutoring needed",
            description = "Need tutor for high school mathematics",
            position = LatLng(48.1500, 11.5600),
            category = MapCategory.TUTORING,
            author = "Sarah L.",
            isPost = true
        ),
        MapLocation(
            id = "post6",
            title = "Companion for elderly parent",
            description = "Looking for someone to spend time with my elderly mother",
            position = LatLng(48.1250, 11.5800),
            category = MapCategory.ELDERLY_HELP,
            author = "Michael B.",
            isPost = true
        ),
        MapLocation(
            id = "post7",
            title = "Help moving furniture",
            description = "Need help moving to new apartment this weekend",
            position = LatLng(48.1600, 11.5700),
            category = MapCategory.MOVING_HELP,
            author = "Julia R.",
            isPost = true
        ),
        MapLocation(
            id = "post8",
            title = "Grocery shopping assistance",
            description = "Elderly person needs help with weekly grocery shopping",
            position = LatLng(48.1300, 11.5550),
            category = MapCategory.ELDERLY_HELP,
            author = "Franz H.",
            isPost = true
        )
    )
}