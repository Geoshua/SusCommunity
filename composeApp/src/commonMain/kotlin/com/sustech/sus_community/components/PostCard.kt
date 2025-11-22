package com.sustech.sus_community.ui

import com.sustech.sus_community.data.Post
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.ui.Alignment

@Preview
@Composable
fun PostCard(
    post: Post,
    onAccept: (Int) -> Unit,
    isSaved: Boolean,
    onToggleSaved: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                post.title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "by ${post.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            Text(
                post.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "\uD83D\uDCCC ${post.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!post.accepted) {
                    Button(
                        onClick = { onAccept(post.id) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Accept Request")
                    }
                } else {
                    Text(
                        "Accepted",
                        color = Color(0xFF34C759),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.width(12.dp))

                IconButton(
                    onClick = { onToggleSaved(post.id) }
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks,
                        contentDescription = if (isSaved) "Saved ✓" else "+Save"
                    )
                }
            }
        }
    }
}
