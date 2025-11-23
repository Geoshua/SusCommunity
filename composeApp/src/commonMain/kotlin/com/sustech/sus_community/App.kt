package com.sustech.sus_community

import androidx.compose.runtime.*
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag
import com.sustech.sus_community.screens.HomeScreen
import com.sustech.sus_community.ui.CreatePostScreen
import com.sustech.sus_community.screens.MapScreen
import com.sustech.sus_community.screens.PostDetailScreen

fun fakePosts() = listOf(
    Post(
        id = 1,
        author = "Alice Swift",
        title = "Trash Pickup event",
        description = "Join us for a community clean-up around Dorm 3! We’ll provide gloves, bags, and snacks.\n" +
                "All students, volunteers, and newcomers are welcome — let’s make our campus greener together!",
        location = "Park Ave 23",
        tags = listOf(PostTag.AskHelp, PostTag.Newcomer),
        image = ""
    ),
    Post(
        id = 2,
        author = "Ben Hoffer",
        title = "Cat sitting",
        description = "Join our cleanup event",
        location = "Riverside Park",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
        image = "https://www.vintagetreecare.com/wp-content/uploads/2023/06/planting-tree-1024x683.jpg"
    ),
    Post(
        id = 3,
        author = "Chen Li",
        title = "Offering free German class",
        description = "If you need help",
        location = "Library",
        tags = listOf(PostTag.OfferHelp, PostTag.Newcomer),
        image = "https://www.skh.com/wp-content/uploads/2025/01/SKHTreePlantingGuide1-min.jpg"
    ),
    Post(
        id = 4,
        author = "Dana",
        title = "Tree planting",
        description = "We’re planting 50 new trees this weekend in the SUSTech Garden area.\n" +
                "No experience needed! Just bring your energy and a smile. Tools and refreshments are provided.",
        location = "Park",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
        image = "https://greggvanourek.com/wp-content/uploads/2023/08/Nature-path-by-water-trees-and-mountains-AdobeStock_291242770-scaled.jpeg"
    ),
    Post(
        id = 4,
        author = "Dana 2",
        title = "Free Tutoring",
        description = "Ever wonder what happens to your plastic bottles? Come learn proper recycling habits and how to reduce daily waste.\n" +
                "Free eco-kits for all participants!",
        location = "Park",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
        image = "https://greggvanourek.com/wp-content/uploads/2023/08/Nature-path-by-water-trees-and-mountains-AdobeStock_291242770-scaled.jpeg"
    ),
    Post(
        id = 4,
        author = "Dana 3",
        title = "Selling stuff",
        description = "Looking for volunteers!",
        location = "Park",
        tags = listOf(PostTag.Event, PostTag.Volunteer),
        image = "https://greggvanourek.com/wp-content/uploads/2023/08/Nature-path-by-water-trees-and-mountains-AdobeStock_291242770-scaled.jpeg"
    )
)


@Composable
fun App() {
    var screen by remember { mutableStateOf("home") }
    var posts by remember { mutableStateOf(fakePosts()) }
    var savedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    when (screen) {
        "home" -> HomeScreen(
            posts = posts,
            onAccept = { id ->
                posts = posts.map { if (it.id == id) it.copy(accepted = true) else it }
            },
            onCreatePost = { screen = "create" },
            onClickDetails = { post ->
                selectedPost = post
                screen = "details"
            },
            savedIds = savedIds,
            onToggleSaved = { id ->
                savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
            }
        )

        "create" -> CreatePostScreen(
            onSubmit = { title, desc, tags, location, image ->
                posts = posts + Post(
                    id = posts.size + 1,
                    author = "You",
                    title = title,
                    description = desc,
                    tags = tags,
                    location = location,
                    image = image
                )
                screen = "home"
            },
            onCancel = { screen = "home" }
        )

        "map" -> MapScreen(
            onBack = { screen = "home"}
        )

        "details" -> {
            selectedPost?.let { post ->
                PostDetailScreen(
                    post = post,
                    isSaved = savedIds.contains(post.id),
                    onBack = { screen = "home" },
                    onToggleSaved = {
                        savedIds = if (savedIds.contains(post.id)) {
                            savedIds - post.id
                        } else {
                            savedIds + post.id
                        }
                    },
                    onAccept = {
                        posts = posts.map { if (it.id == post.id) it.copy(accepted = true) else it }
                        screen = "home"
                    }
                )
            }
        }
    }
}