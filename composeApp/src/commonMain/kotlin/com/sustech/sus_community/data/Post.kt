package com.sustech.sus_community.data

import org.jetbrains.compose.resources.DrawableResource

enum class PostTag(val label: String) {
    OfferHelp("Offer help"),
    AskHelp("Ask help"),
    Event("Event"),
    Volunteer("Volunteer"),
    Newcomer("Newcomer"),
}

data class Post(
    val id: Int,
    val author: String,
    val title: String,
    val description: String,
    val tags: List<PostTag>,
    val location: String,
    val accepted: Boolean = false,
    val image: String
)