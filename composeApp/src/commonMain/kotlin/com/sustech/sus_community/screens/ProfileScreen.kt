package com.sustech.sus_community.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sustech.sus_community.models.Gender
import com.sustech.sus_community.models.GreenTitle
import com.sustech.sus_community.models.User
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.sustech.sus_community.models.UserRole


@Composable
fun ProfileScreen(user: User) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Gradient header
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(Color(0xFF06402B)
                        )
                )
                // Overlay: avatar, name, username, and stats on top of gradient
                val initials = initialsFor(user)
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .height(220.dp)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        // Under white circle (full size)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                        )

                        // Upper smaller light green circle
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBBB791))
                        )
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.displayLarge,
                            color = Color(0xFF0F1210),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Name + username
                    Column(
                        modifier = Modifier.height(150.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = user.displayName?.takeIf { it.isNotBlank() } ?: user.username,
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimary
                        )


                        Spacer(Modifier.height(12.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            user.age?.let { StatChip(label = "Age", value = it.toString(), headerStyle = true) }
                            user.gender?.let { StatChip(label = "Gender", value = genderLabel(it), headerStyle = true) }

                        }
                        StatChip(label = "Role", value = roleLabel(user.role), headerStyle = true)
                    }
                }
            }
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Bio section
                user.bio?.takeIf { it.isNotBlank() }?.let { bio ->
                    SectionCard(title = "About") {
                        Text(bio, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Impact section
                SectionCard(title = "Impact") {
                    val score = user.sustainabilityScore
                    val current = user.calculateGreenTitle()
                    val next = nextThreshold(score)

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Score: $score", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(12.dp))
                            Text("Title: ${prettyGreenTitle(current)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(10.dp))
                        GradientProgressBar(progress = next.progress)
                        Spacer(Modifier.height(6.dp))
                        Text(next.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)


                    }
                }

                Spacer(Modifier.height(12.dp))

                // Badges & Awards section
                val badges = badgesFor(user)
                if (badges.isNotEmpty()) {
                    SectionCard(title = "Badges & Awards") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            badges.forEach { badge ->
                                BadgeChip(badge)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatChip(label: String?, value: String, headerStyle: Boolean = false) {
    val labelColor = if (headerStyle) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    AssistChip(
        onClick = {},
        label = { Text(text = "$label: $value", style = MaterialTheme.typography.labelLarge) },
        colors = AssistChipDefaults.assistChipColors(labelColor = labelColor)
    )
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun GradientProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFFC7FAE7))
                    )
                )
        )
    }
}

private data class ProgressInfo(val progress: Float, val label: String)

private fun nextThreshold(score: Int): ProgressInfo {
    val thresholds = listOf(
        0 to GreenTitle.BEGINNER,
        100 to GreenTitle.ECO_CONSCIOUS,
        250 to GreenTitle.GREEN_WARRIOR,
        500 to GreenTitle.SUSTAINABILITY_HERO,
        1000 to GreenTitle.PLANET_CHAMPION
    )
    val idx = thresholds.indexOfLast { score >= it.first }.coerceAtLeast(0)
    val current = thresholds[idx]
    val next = thresholds.getOrNull(idx + 1)
    return if (next == null) {
        ProgressInfo(1f, "Max title reached: ${prettyGreenTitle(GreenTitle.PLANET_CHAMPION)}")
    } else {
        val range = (next.first - current.first).coerceAtLeast(1)
        val p = ((score - current.first).toFloat() / range).coerceIn(0f, 1f)
        ProgressInfo(p, "${next.first - score} points to ${prettyGreenTitle(next.second)}")
    }
}

private fun prettyGreenTitle(title: GreenTitle): String =
    title.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun initialsFor(user: User): String {
    val name = user.displayName?.takeIf { it.isNotBlank() } ?: user.username
    val parts = name.trim().split(" ")
    val first = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar()
    val second = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar()
    return buildString {
        if (first != null) append(first)
        if (second != null) append(second)
    }.ifBlank { name.firstOrNull()?.uppercaseChar()?.toString() ?: "U" }
}

private fun genderLabel(gender: Gender): String = when (gender) {
    Gender.MALE -> "M"
    Gender.FEMALE -> "F"
    Gender.NON_BINARY -> "NB"
}

private fun roleLabel(role: UserRole): String = when (role) {
    UserRole.NEW_MUENCHER -> "Newcomer"
    UserRole.OLD_MUENCHER -> "Local"
}

// --- Badges/Awards ---

private data class Badge(
    val emoji: String,
    val title: String,
    val subtitle: String? = null
)

@Composable
private fun BadgeChip(badge: Badge) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(110.dp)
            .height(120.dp) // fixed height so all badges have identical size
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = badge.emoji, fontSize = 32.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            badge.subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun badgesFor(user: User): List<Badge> {
    val list = mutableListOf<Badge>()

    // Green title badge
    val greenEmoji = when (user.calculateGreenTitle()) {
        GreenTitle.BEGINNER -> "🌱"
        GreenTitle.ECO_CONSCIOUS -> "🍃"
        GreenTitle.GREEN_WARRIOR -> "🌿"
        GreenTitle.SUSTAINABILITY_HERO -> "🌳"
        GreenTitle.PLANET_CHAMPION -> "🌍"
    }
    list += Badge(
        emoji = greenEmoji,
        title = prettyGreenTitle(user.calculateGreenTitle()),
        subtitle = "Eco score ${user.sustainabilityScore}"
    )

    // Goodwill helper milestones
    val goodwill = user.goodwillPoints
    val goodwillBadge = when {
        goodwill >= 500 -> Badge("🏅", "Helper Champion", "${goodwill} pts")
        goodwill >= 200 -> Badge("🥇", "Gold Helper", "${goodwill} pts")
        goodwill >= 100 -> Badge("🥈", "Silver Helper", "${goodwill} pts")
        goodwill >= 50 -> Badge("🥉", "Bronze Helper", "${goodwill} pts")
        goodwill >= 10 -> Badge("🤝", "Helping Hand", "${goodwill} pts")
        else -> null
    }
    if (goodwillBadge != null) list += goodwillBadge

    // Pet lover badge
    if (user.hasPets) {
        val icon = when {
            user.petTypes.any { it.contains("dog", ignoreCase = true) } -> "🐶"
            user.petTypes.any { it.contains("cat", ignoreCase = true) } -> "🐱"
            else -> "🐾"
        }
        list += Badge(icon, "Pet Lover")
    }

    // Role-based badge
    list += when (user.role) {
        UserRole.NEW_MUENCHER -> Badge("🆕", "New Muencher")
        UserRole.OLD_MUENCHER -> Badge("🏘️", "Local Guide")
    }

    return list
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        user = User(
            username = "muench_user",
            displayName = "Mia Muench",
            role = UserRole.OLD_MUENCHER,
            age = 28,
            gender = Gender.FEMALE,
            hasPets = true,
            petTypes = listOf("dog"),
            sustainabilityScore = 180,
            goodwillPoints = 25,
            bio = "Love helping newcomers settle in Munich. Eco-enthusiast and dog mom.",
            createdAt = "2025-01-12"
        )
    )
}