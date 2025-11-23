package com.sustech.sus_community

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        DashboardScreenWeb()
    }
}