package com.sustech.sus_community.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import com.sustech.sus_community.data.PostTag
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreatePostScreen(
    onSubmit: (String, String, List<PostTag>, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<PostTag>>(emptyList()) }
    var imageUrl by remember { mutableStateOf("") }

    val titleError = title.isBlank()
    val descError = desc.isBlank()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Post", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState),   // ⭐ Make content scrollable
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // HEADER
            Text(
                "Post Information",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            // TITLE
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                isError = titleError,
                supportingText = {
                    if (titleError) Text("Title is required", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // DESCRIPTION
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description *") },
                isError = descError,
                supportingText = {
                    if (descError) Text("Description is required", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            // LOCATION
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            // IMAGE URL
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            // ⭐ IMAGE PREVIEW — SCROLLS NICELY ⭐
            if (imageUrl.isNotBlank()) {
                KamelImage(
                    resource = asyncPainterResource(imageUrl),
                    contentDescription = "Image Preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    onFailure = {
                        Text(
                            "Unable to load image.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                )
            }

            // TAGS
            Column {
                Text(
                    "Type",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PostTag.values().forEach { tag ->
                        FilterChip(
                            selected = tags.contains(tag),
                            onClick = {
                                tags = if (tag in tags) tags - tag else tags + tag
                            },
                            label = { Text(tag.label) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // BUTTON ROW
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (!titleError && !descError) {
                            onSubmit(title, desc, tags, location, imageUrl)
                        }
                    },
                    enabled = !titleError && !descError,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Submit")
                }
            }

            Spacer(Modifier.height(30.dp)) // Breathing space at bottom
        }
    }
}
