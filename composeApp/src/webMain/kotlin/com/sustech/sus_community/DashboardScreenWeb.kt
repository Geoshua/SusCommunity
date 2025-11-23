package com.sustech.sus_community



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color

import com.sustech.sus_community.data.Post
import com.sustech.sus_community.models.Gender
import com.sustech.sus_community.models.User
import com.sustech.sus_community.models.UserRole
import com.sustech.sus_community.screens.MapScreen
import com.sustech.sus_community.screens.PostDetailsScreen
import com.sustech.sus_community.screens.ProfileScreen
import com.sustech.sus_community.ui.CreatePostScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource


private val SusDarkGreen = Color(0x0E1E16) // Darker variant for shadow
private val SusGreen = Color(0xFF49796B)

enum class MiddleView {
    POSTS,
    CREATE,
    DETAILS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenWeb() {

    var posts by remember { mutableStateOf(fakePosts()) }

    // Track whether middle column shows posts or create post UI
    var middleView by remember { mutableStateOf(MiddleView.POSTS) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    val currentUser = remember {
        User(
            username = "johndoe",
            displayName = "John Doe",
            role = UserRole.OLD_MUENCHER,
            age = 28,
            gender = Gender.MALE,
            hasPets = true,
            petTypes = listOf("Dog"),
            sustainabilityScore = 180,
            goodwillPoints = 42,
            bio = "Old Müncher happy to help newcomers settle in. Love biking and zero‑waste living!",
            createdAt = "2024-11-01"
        )
    }


    val onAccept: (Int) -> Unit = { id ->
        posts = posts.map { p -> if (p.id == id) p.copy(accepted = true) else p }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    KamelImage(
                        resource = asyncPainterResource("logo.png"),
                        contentDescription = "Logo"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SusGreen,
                    titleContentColor = Color.White
                ),
                modifier = Modifier.height(56.dp)
            )
            Divider(color = SusDarkGreen, thickness = 3.dp)
        }
    ) { padding ->

        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // LEFT COLUMN — unchanged
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                ProfileScreen(currentUser)
            }

            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {

                when (middleView) {

                    MiddleView.POSTS -> {
                        PostsGridScreen(
                            posts = posts,
                            onAccept = onAccept,
                            onClickDetails = { post ->
                                selectedPost = post
                                middleView = MiddleView.DETAILS
                            },
                            onCreatePost = { middleView = MiddleView.CREATE }
                        )
                    }

                    MiddleView.CREATE -> {
                        CreatePostScreen(
                            onSubmit = { title, desc, tags, location, image ->

                                val new = Post(
                                    id = posts.maxOf { it.id } + 1,
                                    title = title,
                                    description = desc,
                                    tags = tags,
                                    location = location,
                                    image = image,
                                    accepted = false,
                                    author = "sfsjdk"
                                )

                                posts = posts + new
                                middleView = MiddleView.POSTS
                            },

                            onCancel = {
                                middleView = MiddleView.POSTS
                            }
                        )
                    }
                    MiddleView.DETAILS -> {
                        selectedPost?.let { post ->
                            PostDetailsScreen(
                                post = post,
                                onBack = { middleView = MiddleView.POSTS }
                            )
                        }
                    }
                }
            }

            // RIGHT COLUMN — unchanged
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                KamelImage(
                    resource = asyncPainterResource("map.png"),
                    contentDescription = "Map"
                )
            }
        }
    }
}