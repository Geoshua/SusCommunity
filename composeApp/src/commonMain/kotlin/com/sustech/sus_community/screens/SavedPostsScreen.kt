package com.sustech.sus_community.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.ui.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    posts: List<Post>,
    savedIds: Set<Int>,
    onAccept: (Int) -> Unit,
    onToggleSaved: (Int) -> Unit
) {
    val savedPosts = remember(posts, savedIds) { posts.filter { savedIds.contains(it.id) } }

    Scaffold(topBar = { TopAppBar(title = { Text("Saved") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            if (savedPosts.isEmpty()) {
                Text("Nothing saved yet. Save posts from the feed to see them here.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    items(savedPosts) { post ->
                        PostCard(
                            post = post,
                            onAccept = onAccept,
                            isSaved = true,
                            onToggleSaved = onToggleSaved
                        )
                    }
                }
            }
        }
    }
}