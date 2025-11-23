package com.sustech.sus_community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.screens.HomeScreen
import com.sustech.sus_community.screens.SavedScreen
import com.sustech.sus_community.ui.CreatePostScreen
import com.sustech.sus_community.fakePosts
import com.sustech.sus_community.screens.MapScreen

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

    Scaffold(
        bottomBar = {
            NavigationBar {
                AndroidTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        alwaysShowLabel = true,
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
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
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
                    }
                )

                AndroidTab.Create -> CreatePostScreen(
                    onSubmit = { title, desc, tags, location ->
                        posts = posts + Post(
                            id = posts.size + 1,
                            author = "You",
                            title = title,
                            description = desc,
                            tags = tags,
                            location = location
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
                    }
                )
                AndroidTab.Map -> PlaceholderScreen("Map coming soon")
                AndroidTab.Profile -> PlaceholderScreen("Profile coming soon")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
