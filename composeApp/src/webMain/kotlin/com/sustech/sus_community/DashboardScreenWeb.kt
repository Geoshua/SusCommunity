package com.sustech.sus_community


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import com.sustech.sus_community.screens.HomeScreen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

private val SusBlue = Color(0xFF1A73E8)     // Google-style blue
private val SusDarkBlue = Color(0xFF1558B0) // Darker variant for shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenWeb() {

    var posts by remember { mutableStateOf(fakePosts()) }

    val onAccept: (Int) -> Unit = { id: Int ->
        posts = posts.map { post ->
            if (post.id == id) post.copy(accepted = true) else post
        }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            "SUS Community",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SusBlue,
                    titleContentColor = Color.White
                ),
                modifier = Modifier
                    .padding(0.dp)
                    .height(56.dp)
            )
            Divider(color = SusDarkBlue, thickness = 3.dp)
        }
    ) { padding ->

        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // LEFT COLUMN — PROFILE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                HomeScreen(
                    posts = posts,
                    onAccept = onAccept,
                    onCreatePost = {},
                    savedIds = emptySet(),
                    onToggleSaved = {}
                )
            }

            // MIDDLE COLUMN — POSTS FEED (scrollable)
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                PostsGridScreen(
                    posts = posts,
                    onAccept = onAccept,
                    onCreatePost = {} // disable FAB for web
                )
            }

            // RIGHT COLUMN — MAP
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                HomeScreen(
                    posts = posts,
                    onAccept = onAccept,
                    onCreatePost = {},
                    savedIds = emptySet(),
                    onToggleSaved = {}
                )
            }
        }
    }
}

