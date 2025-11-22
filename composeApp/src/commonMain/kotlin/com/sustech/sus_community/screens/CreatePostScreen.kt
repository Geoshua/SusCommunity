package com.sustech.sus_community.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.PostTag


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onSubmit: (String, String, List<PostTag>, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<PostTag>>(emptyList()) }
    var image by remember { mutableStateOf("") }
    // var image by remember { mutableStateOf<DrawableResource>(Res.drawable.Pla) }
    // val imagePath: DrawableResource = Res.drawable.default


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Post") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            // IMAGE URL INPUT
            OutlinedTextField(
                value = image,
                onValueChange = { image = it },
                label = { Text("Image URL (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // TAG SELECTION
            Text("Type", style = MaterialTheme.typography.titleMedium)

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostTag.values().forEach { tag ->
                    AssistChip(
                        onClick = {
                            tags = if (tag in tags) tags - tag else tags + tag
                        },
                        label = { Text(tag.label) },
                        leadingIcon = {
                            if (tag in tags) Text("✔")
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank() && desc.isNotBlank()) {
                            onSubmit(title, desc, tags, location, image)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Submit") }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }
            }
        }
    }
}