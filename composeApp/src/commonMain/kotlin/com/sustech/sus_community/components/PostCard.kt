package com.sustech.sus_community.ui

import com.sustech.sus_community.data.Post
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.unit.dp
import io.kamel.core.utils.URI
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.ui.Alignment
import com.sustech.sus_community.models.PostTag

@Preview
@Composable
fun PostCard(
    post: Post,
    onAccept: (Int) -> Unit,
    isSaved: Boolean,
    onToggleSaved: (Int) -> Unit,
    onClickDetails: (Post) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {

            KamelImage(
                resource = {
                    asyncPainterResource(data = URI(post.image))
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(6.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop, // crops vertical images nicely
                onLoading = { CircularProgressIndicator() },
                onFailure = {
                    KamelImage(
                        resource = asyncPainterResource("https://picsum.photos/800/800"),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            )
            // CONTENT AREA
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                // ✔ Title styling—cleaner & stronger
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(Modifier.height(4.dp))

                // ✔ Author in lighter tone and smaller font
                Text(
                    "by ${post.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Divider(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )
                Spacer(Modifier.height(12.dp))

                // BUTTON ROW
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✔ Flatter button style
                    Button(
                        onClick = { onClickDetails(post) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (post.tags.contains(com.sustech.sus_community.data.PostTag.Event)) {
                            Text("Join")
                        } else {
                            Text("Learn More")
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // Bookmark button
                    IconButton(onClick = { onToggleSaved(post.id) }) {
                        Icon(
                            imageVector = if (isSaved)
                                Icons.Filled.Bookmarks
                            else Icons.Outlined.Bookmarks,
                            contentDescription = if (isSaved) "Saved ✓" else "Save"
                        )
                    }
                }
            }
        }
    }
}
