package com.sustech.sus_community.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sustech.sus_community.data.Post
import com.sustech.sus_community.data.PostTag
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    post: Post,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //  HEADER IMAGE
            if (post.image.isNotBlank()) {
                KamelImage(
                    resource = asyncPainterResource(post.image),
                    contentDescription = post.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            //  AUTHOR
            Text(
                text = "By ${post.author}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            //  TAGS
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                post.tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag.label) }
                    )
                }
            }

            Divider()

            //  DESCRIPTION
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyLarge
            )

            // ⭐ LOCATION
            if (post.location.isNotBlank()) {
                Text(
                    text = "📍 ${post.location}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  (Optional future fields)
            // If you want: date, time, RSVP count, comments, organizer, contact info...
            // I can add extra components here.

            Spacer(Modifier.weight(1f, fill = false))

            //  ACTION BUTTON (Join / Contact)
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                when {
                    post.tags.contains(PostTag.Event) -> Text("Join Event")
                    post.tags.contains(PostTag.AskHelp) -> Text("Offer Help")
                    post.tags.contains(PostTag.OfferHelp) -> Text("Get Help")
                    else -> Text("Contact")
                }
            }
        }
    }
}
