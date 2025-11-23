package com.sustech.sus_community.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag
import com.sustech.sus_community.ui.PostCard
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: List<Post>,
    onAccept: (Int) -> Unit,
    onCreatePost: () -> Unit,
    savedIds: Set<Int>,
    onToggleSaved: (Int) -> Unit,
    onClickDetails: (Post) -> Unit
) {
    var filter by remember { mutableStateOf<PostTag?>(null) } // null = show all

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize()
    ) {
        Text("Latest", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)

        // FILTER ROW
        FilterRow(
            selected = filter,
            onSelect = { filter = it }
        )

        Spacer(Modifier.height(20.dp))

        val filteredPosts =
            if (filter == null) posts
            else posts.filter { post ->
                post.tags.contains(filter)     // <-- FIX
            }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(filteredPosts) { post ->
                PostCard(
                    post = post,
                    onAccept = onAccept,
                    isSaved = savedIds.contains(post.id),
                    onToggleSaved = onToggleSaved,
                    onClickDetails = onClickDetails
                )
            }
        }
    }
}



@Composable
fun FilterRow(
    selected: PostTag?,
    onSelect: (PostTag?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            label = "All",
            selected = selected == null,
            onClick = { onSelect(null) }
        )
        FilterChip(
            label = "Events",
            selected = selected == PostTag.Event,
            onClick = { onSelect(PostTag.Event) }
        )
        FilterChip(
            label = "Requests",
            selected = selected == PostTag.AskHelp,
            onClick = { onSelect(PostTag.AskHelp) }
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(if (selected) "✔ $label" else label) }
    )
}