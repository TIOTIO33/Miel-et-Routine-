package com.example.ui.screens.kids

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Badge
import com.example.data.model.ChildBadgeUnlock
import com.example.data.model.ChildProfile
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SkyBlueLight

@Composable
fun KidsBadgesScreen(
    currentChild: ChildProfile?,
    allBadges: List<Badge>,
    unlockedBadges: List<ChildBadgeUnlock>,
    modifier: Modifier = Modifier
) {
    val level = currentChild?.level ?: 1
    val levelTitle = currentChild?.levelTitle ?: "Petite Abeille"
    val totalPoints = currentChild?.totalPointsEarned ?: 0
    val missionsCount = currentChild?.completedMissionsCount ?: 0

    val nextLevelTarget = level * 25
    val currentLevelBase = (level - 1) * 25
    val progressInLevel = ((totalPoints - currentLevelBase).toFloat() / 25f).coerceIn(0f, 1f)

    val unlockedKeys = unlockedBadges.map { it.badgeKey }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Level & Progression Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = HoneyLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Niveau $level",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoneyPrimaryVariant
                    )

                    Text(
                        text = levelTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { progressInLevel },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = HoneyPrimary,
                        trackColor = Color(0xFFFDE68A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$totalPoints miel récolté au total",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Objectif niveau ${level + 1} : $nextLevelTarget miel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mini stats pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(text = "✅", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$missionsCount missions finies",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F2937)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(text = "🎖️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${unlockedKeys.size}/${allBadges.size} badges",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F2937)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Collection de Badges & Trophées :",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        items(allBadges) { badge ->
            val isUnlocked = unlockedKeys.contains(badge.key)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color.White else Color(0xFFF9FAFB)
                ),
                border = if (isUnlocked) {
                    androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBBF24))
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                },
                elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                if (isUnlocked) HoneyLight else Color(0xFFE5E7EB),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isUnlocked) {
                                when (badge.iconKey) {
                                    "emoji_events" -> "🥇"
                                    "military_tech" -> "🎖️"
                                    "stars" -> "⭐"
                                    "favorite" -> "💖"
                                    "workspace_premium" -> "👑"
                                    "diamond" -> "💎"
                                    else -> "🏆"
                                }
                            } else {
                                "🔒"
                            },
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUnlocked) Color(0xFF1F2937) else Color.Gray
                            )
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "✨ Débloqué !", fontSize = 11.sp, color = HoneyPrimaryVariant, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badge.description,
                            fontSize = 12.sp,
                            color = if (isUnlocked) Color(0xFF4B5563) else Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
