package com.example.ui.screens.kids

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import com.example.data.model.PeriodOfDay
import com.example.ui.components.BeeMascotView
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.HoneyPotCounter
import com.example.ui.theme.HoneyBackground
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.KidPillGreen
import com.example.ui.theme.SageGreenLight
import java.util.Calendar

@Composable
fun KidsHomeScreen(
    currentChild: ChildProfile?,
    allChildren: List<ChildProfile>,
    missions: List<Mission>,
    completions: List<MissionCompletion>,
    hiveCode: String? = null,
    isCloudConnected: Boolean = false,
    onOpenFamilyLink: () -> Unit = {},
    onSelectChild: (Long) -> Unit,
    onToggleMission: (Mission, ChildProfile) -> Unit,
    onOpenBeeCustomizer: () -> Unit,
    onOpenParentSpace: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf<PeriodOfDay?>(null) } // null = all

    // Current day of week (1=Monday ... 7=Sunday)
    val currentDayOfWeek = remember {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_WEEK)
        // Calendar: Sunday=1, Monday=2... Saturday=7
        if (d == Calendar.SUNDAY) 7 else d - 1
    }

    // Filter missions for today and current child based on age
    val childAge = currentChild?.age ?: 5
    val todayMissions = missions.filter { m ->
        val matchesChild = if (m.childId == 0L) {
            m.isAppropriateForAge(childAge)
        } else {
            m.childId == currentChild?.id
        }
        matchesChild && m.appliesToDay(currentDayOfWeek)
    }

    val filteredMissions = if (selectedPeriod == null) {
        todayMissions
    } else {
        todayMissions.filter { it.period == selectedPeriod }
    }

    val completedCount = todayMissions.count { m ->
        completions.any { c -> c.missionId == m.id && c.childId == currentChild?.id && c.isEffectivelyCompleted }
    }
    val totalCount = todayMissions.size
    val dailyProgress = if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 1f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Child Selector chips with age
                Row(verticalAlignment = Alignment.CenterVertically) {
                    allChildren.forEach { child ->
                        val isSelected = child.id == currentChild?.id
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) HoneyPrimary else Color.White,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clickable { onSelectChild(child.id) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (child.name.startsWith("L", ignoreCase = true)) "👦" else "👧",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${child.name} (${child.age}a)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF1F2937)
                                )
                            }
                        }
                    }
                }

                // Points & Parent Gate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HoneyPotCounter(
                        points = currentChild?.currentPoints ?: 0,
                        level = currentChild?.level
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Cloud Multi-Phone sync chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCloudConnected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clickable(onClick = onOpenFamilyLink)
                            .testTag("kids_header_sync_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)
                        ) {
                            Text(text = if (isCloudConnected) "🟢" else "⚪", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (!hiveCode.isNullOrBlank()) hiveCode else "Lier",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCloudConnected) Color(0xFF15803D) else Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onOpenParentSpace,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFFEF3C7), CircleShape)
                            .testTag("parent_gate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Accès Espace Parents",
                            tint = HoneyPrimaryVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Hero Card with Meadow Banner & Bee Mascot
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background Illustration
                    Image(
                        painter = painterResource(id = R.drawable.banner_honey_meadow),
                        contentDescription = "Prairie fleurie de la ruche",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    )

                    // Soft gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.85f),
                                        Color.White
                                    )
                                )
                            )
                    )

                    // Mascot & Message
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BeeMascotView(
                            hat = currentChild?.beeHat ?: "none",
                            colorTheme = currentChild?.beeColor ?: "honey_gold",
                            size = 85,
                            showSpeechBubble = false,
                            onClick = onOpenBeeCustomizer
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Bonjour ${currentChild?.name ?: "Champion"} ! 🐝",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1F2937)
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF3C7),
                                    modifier = Modifier.clickable { onOpenBeeCustomizer() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Palette,
                                            contentDescription = null,
                                            tint = HoneyPrimaryVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Look",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HoneyPrimaryVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (completedCount == totalCount && totalCount > 0) {
                                    "Incroyable ! Tu as tout terminé aujourd'hui ! 🌟"
                                } else {
                                    "Chaque mission te fait grandir et gagner du miel !"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Day progress bar
                            LinearProgressIndicator(
                                progress = { dailyProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = KidPillGreen,
                                trackColor = Color(0xFFE5E7EB)
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedCount sur $totalCount missions réussies aujourd'hui",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }
        }

        // Period filter pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAll = selectedPeriod == null
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAll) HoneyPrimary else Color.White,
                        shadowElevation = if (isAll) 2.dp else 1.dp,
                        modifier = Modifier.clickable { selectedPeriod = null }
                    ) {
                        Text(
                            text = "⭐ Tout le jour",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAll) Color.White else Color(0xFF4B5563),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(PeriodOfDay.values()) { period ->
                    val isSelected = selectedPeriod == period
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) HoneyPrimary else Color.White,
                        shadowElevation = if (isSelected) 2.dp else 1.dp,
                        modifier = Modifier.clickable { selectedPeriod = period }
                    ) {
                        Text(
                            text = "${period.emoji} ${period.labelFr}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF4B5563),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Missions List
        if (filteredMissions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rien de prévu pour ce moment !",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Profite bien de ta journée ou explore d'autres missions.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredMissions, key = { it.id }) { mission ->
                val completion = completions.firstOrNull {
                    it.missionId == mission.id && it.childId == currentChild?.id
                }
                val isCompleted = completion?.isEffectivelyCompleted == true
                val isPending = completion?.status == "PENDING_PARENT"

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) SageGreenLight.copy(alpha = 0.6f) else Color.White
                    ),
                    border = if (isCompleted) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF34D399))
                    } else if (isPending) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24))
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3EAD9))
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_card_${mission.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Icon Badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    mission.category.badgeColor.copy(alpha = 0.18f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIconByKey(mission.iconKey),
                                contentDescription = null,
                                tint = mission.category.badgeColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Mission Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CategoryIconHelper.getEmojiForCategory(mission.category) + " " + mission.category.labelFr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = mission.category.badgeColor
                                )
                                if (mission.specificTime != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "⏰ ${mission.specificTime}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = mission.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                            )

                            if (mission.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mission.description,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Badges row: Points + Age range + Parent control indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Points pill
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = HoneyLight
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = "🍯", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "+${mission.points}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = HoneyPrimaryVariant
                                        )
                                    }
                                }

                                // Age range pill
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = mission.ageRangeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                // Parent validation notice
                                if (mission.requiresParentValidation || currentChild?.requiresParentValidation == true) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = "🛡️ Parent",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            if (isPending) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⏳ Notifié à papa/maman pour accord",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Large Action Button
                        if (currentChild != null) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> KidPillGreen
                                            isPending -> Color(0xFFFBBF24)
                                            else -> Color(0xFFFFFBEB)
                                        }
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = when {
                                            isCompleted -> KidPillGreen
                                            isPending -> Color(0xFFF59E0B)
                                            else -> HoneyPrimary
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onToggleMission(mission, currentChild)
                                    }
                                    .testTag("mission_toggle_${mission.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isCompleted -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Mission accomplie",
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                    isPending -> {
                                        Icon(
                                            imageVector = Icons.Default.HourglassTop,
                                            contentDescription = "En attente validation",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Fait !",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = HoneyPrimaryVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
