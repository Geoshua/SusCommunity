package com.sustech.sus_community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag
import com.sustech.sus_community.screens.FilterRow
import com.sustech.sus_community.ui.PostCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsGridScreen(
    posts: List<Post>,
    onAccept: (Int) -> Unit,
    onCreatePost: () -> Unit
) {
    var filter by remember { mutableStateOf<PostTag?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Community") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePost) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            // FILTER ROW
            FilterRow(
                selected = filter,
                onSelect = { filter = it }
            )

            Spacer(Modifier.height(20.dp))

            // Filter logic
            val filteredPosts =
                if (filter == null) posts
                else posts.filter { post ->
                    post.tags.contains(filter)
                }

            // -------- GRID VIEW (like Instagram) --------
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 240.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPosts) { post ->
                    PostCard(post, onAccept)
                }
            }
        }
    }
}