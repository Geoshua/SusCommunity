package com.sustech.sus_community

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sustech.sus_community.ui.theme.SusCommunityTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        SusCommunityTheme {
            App()
        }
    }
}