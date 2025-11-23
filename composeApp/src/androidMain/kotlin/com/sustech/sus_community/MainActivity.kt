package com.sustech.sus_community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sustech.sus_community.ui.theme.SusCommunityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SusCommunityTheme {
                AndroidTabbedApp()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    SusCommunityTheme {
        AndroidTabbedApp()
    }
}