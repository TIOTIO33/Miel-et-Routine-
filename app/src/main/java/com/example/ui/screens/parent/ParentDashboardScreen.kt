package com.example.ui.screens.parent

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import com.example.data.model.RewardRedemption
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.KidPillGreen
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParentDashboardScreen(
    children: List<ChildProfile>,
    missions: List<Mission>,
    todayCompletions: List<MissionCompletion>,
    pendingValidations: List<MissionCompletion>,
    allCompletions: List<MissionCompletion>,
    pendingRedemptions: List<RewardRedemption>,
    onApproveValidation: (MissionCompletion) -> Unit,
    onApproveAllValidations: () -> Unit,
    onRejectValidation: (MissionCompletion) -> Unit,
    onApproveRedemption: (Long) -> Unit,
    onRejectRedemption: (Long) -> Unit,
    onNavigateToMissions: () -> Unit,
    onNavigateToRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalTodayCompleted = todayCompletions.count { it.isEffectivelyCompleted }
    var showAllHistory by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Centre de Contrôle Parental 🛡️",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "Supervision sur votre téléphone, validations en attente et journal d'activité",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Summary KPI Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // KPI 1: Missions Completed Today
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SageGreenLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Aujourd'hui", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SageGreenDark)
                            Text(text = "✅", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalTodayCompleted",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = SageGreenDark
                        )
                        Text(text = "missions accomplies", fontSize = 11.sp, color = Color(0xFF065F46))
                    }
                }

                // KPI 2: Validations in waiting
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (pendingValidations.isNotEmpty()) Color(0xFFFEF3C7) else Color(0xFFF3F4F6)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "À valider", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HoneyPrimaryVariant)
                            Text(text = "⏳", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${pendingValidations.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (pendingValidations.isNotEmpty()) HoneyPrimaryVariant else Color.Gray
                        )
                        Text(text = "en attente de votre accord", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        // Pending Validations Section (CRITICAL FEATURE FOR PHONE CONTROL)
        if (pendingValidations.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = HoneyPrimaryVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Alertes téléphone (${pendingValidations.size})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            // 1-Click Approve All
                            Button(
                                onClick = onApproveAllValidations,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KidPillGreen)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tout valider", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Text(
                            text = "Vos enfants ont validé ces tâches. Accordez les points de miel :",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            items(pendingValidations) { comp ->
                val mission = missions.firstOrNull { it.id == comp.missionId }
                val child = children.firstOrNull { it.id == comp.childId }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(HoneyLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (child?.name?.startsWith("L", ignoreCase = true) == true) "👦" else "👧", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${child?.name ?: "Enfant"} (${child?.age ?: 5} ans)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HoneyPrimaryVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = mission?.ageRangeLabel ?: "Tous",
                                        fontSize = 10.sp,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = mission?.title ?: "Mission",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "+${mission?.points ?: 2} miel 🍯 • ${mission?.category?.labelFr ?: "Général"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }

                        // Quick Actions
                        IconButton(
                            onClick = { onRejectValidation(comp) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFEE2E2), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Refuser",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onApproveValidation(comp) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(SageGreenLight, CircleShape)
                                .testTag("approve_validation_${comp.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Valider",
                                tint = SageGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Pending Reward Redemptions (if any)
        if (pendingRedemptions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Demandes de Récompenses (${pendingRedemptions.size}) 🎁",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Voir tout",
                        fontSize = 12.sp,
                        color = HoneyPrimaryVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToRewards() }
                    )
                }
            }

            items(pendingRedemptions) { red ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎁", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${red.childName} souhaite :",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = red.rewardTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Coût : ${red.costPoints} points déduits",
                                fontSize = 11.sp,
                                color = HoneyPrimaryVariant
                            )
                        }

                        Button(
                            onClick = { onApproveRedemption(red.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KidPillGreen)
                        ) {
                            Text("Accorder", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Children Progress Breakdown with Age
        item {
            Text(
                text = "Profils et missions adaptées par âge :",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        items(children) { child ->
            val childTodayDone = todayCompletions.count { it.childId == child.id && it.isEffectivelyCompleted }
            val childTotalMissions = missions.count {
                val matchesChild = if (it.childId == 0L) it.isAppropriateForAge(child.age) else it.childId == child.id
                matchesChild
            }
            val progress = if (childTotalMissions > 0) (childTodayDone.toFloat() / childTotalMissions.toFloat()).coerceIn(0f, 1f) else 0f

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (child.name.startsWith("L", ignoreCase = true)) "👦" else "👧",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${child.name} (${child.age} ans)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (child.age < 7) Color(0xFFFEF3C7) else Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = if (child.age < 7) "👶 Maternelle" else "🎒 Primaire",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (child.age < 7) HoneyPrimaryVariant else Color(0xFF1D4ED8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (child.requiresParentValidation) "🛡️ Contrôle parent actif" else "⚡ Mode autonome",
                                    fontSize = 11.sp,
                                    color = if (child.requiresParentValidation) HoneyPrimaryVariant else SageGreenDark
                                )
                            }
                        }

                        // Points badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HoneyLight
                        ) {
                            Text(
                                text = "${child.currentPoints} 🍯",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = HoneyPrimaryVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$childTodayDone / $childTotalMissions routines aujourd'hui",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = KidPillGreen,
                        trackColor = Color(0xFFF3F4F6)
                    )
                }
            }
        }

        // Live Audit & Control Trail (Historique en direct sur le téléphone parent)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Journal de Contrôle des Validations 📜",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }

                if (allCompletions.size > 5) {
                    Text(
                        text = if (showAllHistory) "Réduire" else "Voir tout (${allCompletions.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoneyPrimaryVariant,
                        modifier = Modifier.clickable { showAllHistory = !showAllHistory }
                    )
                }
            }
        }

        val historyToShow = if (showAllHistory) allCompletions else allCompletions.take(5)
        if (historyToShow.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucune validation enregistrée pour l'instant.\nDès qu'un enfant valide une tâche, vous la verrez ici !",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(historyToShow) { comp ->
                val mission = missions.firstOrNull { it.id == comp.missionId }
                val child = children.firstOrNull { it.id == comp.childId }
                val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                val timeStr = sdf.format(Date(comp.completedTimestamp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (comp.status) {
                                    "APPROVED" -> "✅"
                                    "PENDING_PARENT" -> "⏳"
                                    "REJECTED" -> "❌"
                                    else -> "⚡"
                                },
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${child?.name ?: "Enfant"} (${child?.age ?: 5}a) • ${mission?.title ?: "Mission"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "$timeStr • +${mission?.points ?: 2} miel 🍯",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (comp.status) {
                                "APPROVED" -> SageGreenLight
                                "PENDING_PARENT" -> Color(0xFFFEF3C7)
                                "REJECTED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFEFF6FF)
                            }
                        ) {
                            Text(
                                text = when (comp.status) {
                                    "APPROVED" -> "Validé par parent"
                                    "PENDING_PARENT" -> "En attente"
                                    "REJECTED" -> "Refusé"
                                    else -> "Validé auto"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (comp.status) {
                                    "APPROVED" -> SageGreenDark
                                    "PENDING_PARENT" -> HoneyPrimaryVariant
                                    "REJECTED" -> Color(0xFFDC2626)
                                    else -> Color(0xFF1D4ED8)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
