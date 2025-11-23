package com.sustech.sus_community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.screens.HomeScreen
import com.sustech.sus_community.screens.SavedScreen
import com.sustech.sus_community.screens.ProfileScreen
import com.sustech.sus_community.screens.PostDetailScreen
import com.sustech.sus_community.ui.CreatePostScreen
import com.sustech.sus_community.fakePosts
import com.sustech.sus_community.screens.MapScreen
import com.sustech.sus_community.models.User
import com.sustech.sus_community.models.UserRole
import com.sustech.sus_community.models.Gender

private enum class AndroidTab(val label: String) {
    Feed("Feed"),
    Saved("Saved"),
    Create("Create"),
    Map("Map"),
    Profile("Profile")
}

@Composable
fun AndroidTabbedApp() {
    var selectedTab by remember { mutableStateOf(AndroidTab.Feed) }
    var posts by remember { mutableStateOf<List<Post>>(fakePosts()) }
    var savedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    // Temporary current user until auth/profile storage is added
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

    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        bottomBar = {
            // Bottom tab bar: dark green, rounded top corners, not floating.
            Surface(
                modifier = Modifier.height(90.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                ) {
                    AndroidTab.values().forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab.label) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            ),
                            icon = {
                                val icon = when (tab) {
                                    AndroidTab.Feed -> if (selectedTab == tab) Icons.Filled.Home else Icons.Outlined.Home
                                    AndroidTab.Saved -> if (selectedTab == tab) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks
                                    AndroidTab.Create -> if (selectedTab == tab) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline
                                    AndroidTab.Map -> if (selectedTab == tab) Icons.Filled.Map else Icons.Outlined.Map
                                    AndroidTab.Profile -> if (selectedTab == tab) Icons.Filled.Person else Icons.Outlined.Person
                                }
                                Icon(imageVector = icon, contentDescription = tab.label)
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // We intentionally ignore the bottom inset so content can scroll under the tab bar,
        // making post cards disappear only when reaching the bar.
        // Touch paddingValues to avoid tooling warnings while effectively ignoring it.
        if (paddingValues.hashCode() == Int.MIN_VALUE) { /* no-op */ }
        Box {
            when (selectedTab) {
                AndroidTab.Feed -> HomeScreen(
                    posts = posts,
                    onAccept = { id ->
                        posts = posts.map { if (it.id == id) it.copy(accepted = true) else it }
                    },
                    onCreatePost = { selectedTab = AndroidTab.Create },
                    savedIds = savedIds,
                    onToggleSaved = { id ->
                        savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
                    },
                    onClickDetails = { post ->
                        selectedPost = post
                    }
                )

                AndroidTab.Create -> CreatePostScreen(
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
                        selectedTab = AndroidTab.Feed
                    },
                    onCancel = { selectedTab = AndroidTab.Feed }
                )
                AndroidTab.Saved -> SavedScreen(
                    posts = posts,
                    savedIds = savedIds,
                    onAccept = { id ->
                        posts = posts.map { if (it.id == id) it.copy(accepted = true) else it }
                    },
                    onToggleSaved = { id ->
                        savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
                    },
                    onClickDetails = { post ->
                        selectedPost = post
                    }
                )
                AndroidTab.Map -> MapScreen(
                    onBack = { selectedTab = AndroidTab.Feed }
                )
                AndroidTab.Profile -> ProfileScreen(user = currentUser)
            }

            // Show PostDetailScreen overlay when a post is selected
            selectedPost?.let { post ->
                PostDetailScreen(
                    post = post,
                    isSaved = savedIds.contains(post.id),
                    onBack = { selectedPost = null },
                    onToggleSaved = {
                        savedIds = if (savedIds.contains(post.id)) {
                            savedIds - post.id
                        } else {
                            savedIds + post.id
                        }
                    },
                    onAccept = {
                        posts = posts.map { if (it.id == post.id) it.copy(accepted = true) else it }
                        selectedPost = null
                    }
                )
            }
        }
    }
}
