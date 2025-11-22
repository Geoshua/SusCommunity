package com.sustech.sus_community

import androidx.compose.runtime.*
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag
import com.sustech.sus_community.screens.HomeScreen
import com.sustech.sus_community.ui.CreatePostScreen



fun fakePosts() = listOf(
    Post(
        id = 1,
        author = "Alice",
        title = "Campus cleanup",
        description = "Join us for cleaning!",
        location = "Dorm 3",
        tags = listOf(PostTag.AskHelp, PostTag.Newcomer),
    ),
    Post(
        id = 2,
        author = "Ben",
        title = "Community cleanup event",
        description = "Join our cleanup event",
        location = "Riverside Park",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
    ),
    Post(
        id = 3,
        author = "Chen",
        title = "Offering free German help",
        description = "If you need help",
        location = "Library",
        tags = listOf(PostTag.OfferHelp, PostTag.Newcomer),
    ),
    Post(
        id = 4,
        author = "Dana",
        title = "Volleyball players",
        description = "Looking for two players!",
        location = "Sports Center",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
    )
)


@Composable
fun App() {
    var screen by remember { mutableStateOf("home") }
    var posts by remember { mutableStateOf(fakePosts()) }
    var savedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    when (screen) {
        "home" -> HomeScreen(
            posts = posts,
            onAccept = { id ->
                posts = posts.map { if (it.id == id) it.copy(accepted = true) else it }
            },
            onCreatePost = { screen = "create" },
            savedIds = savedIds,
            onToggleSaved = { id ->
                savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
            }
        )

        "create" -> CreatePostScreen(
            onSubmit = { title, desc, tags, location ->
                posts = posts + Post(
                    id = posts.size + 1,
                    author = "You",
                    title = title,
                    description = desc,
                    tags = tags,
                    location = location
                )
                screen = "home"
            },
            onCancel = { screen = "home" }
        )
    }
}