package com.sustech.sus_community.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.ui.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    posts: List<Post>,
    savedIds: Set<Int>,
    onAccept: (Int) -> Unit,
    onToggleSaved: (Int) -> Unit,
    onClickDetails: (Post) -> Unit
) {
    val savedPosts = remember(posts, savedIds) { posts.filter { savedIds.contains(it.id) } }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize()
        ) {
            Text("Saved", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)

            if (savedPosts.isEmpty()) {
                ElevatedCard(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Nothing saved yet. Save posts from the feed to see them here.", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    items(savedPosts) { post ->
                        PostCard(
                            post = post,
                            onAccept = onAccept,
                            isSaved = true,
                            onToggleSaved = onToggleSaved,
                            onClickDetails = onClickDetails
                        )
                    }
                }
            }
        }
    }
}