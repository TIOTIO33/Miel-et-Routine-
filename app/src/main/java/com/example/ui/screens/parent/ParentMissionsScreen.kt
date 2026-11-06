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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Category
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.PeriodOfDay
import com.example.data.model.RoutineTemplate
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.SageGreenLight

@Composable
fun ParentMissionsScreen(
    children: List<ChildProfile>,
    missions: List<Mission>,
    onSaveMission: (Mission) -> Unit,
    onDeleteMission: (Mission) -> Unit,
    onApplyTemplate: (RoutineTemplate, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChildFilter by remember { mutableStateOf<Long?>(null) } // null = all
    var selectedAgeFilter by remember { mutableStateOf<String?>(null) } // null, "3-5", "6-8", "8+"
    var selectedCategoryFilter by remember { mutableStateOf<Category?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var missionToEdit by remember { mutableStateOf<Mission?>(null) }
    var showTemplatesDialog by remember { mutableStateOf(false) }

    val filteredMissions = missions.filter { m ->
        val childMatch = selectedChildFilter == null || m.childId == 0L || m.childId == selectedChildFilter
        val catMatch = selectedCategoryFilter == null || m.category == selectedCategoryFilter
        val ageMatch = when (selectedAgeFilter) {
            "3-5" -> m.minAge <= 5 && m.maxAge >= 3
            "6-8" -> m.minAge <= 8 && m.maxAge >= 6
            "8+" -> m.maxAge >= 8
            else -> true
        }
        childMatch && catMatch && ageMatch
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Gestion des Missions 📋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "${missions.size} missions configurées • Filtres par âge et enfant",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Pre-made routines template button
                    Button(
                        onClick = { showTemplatesDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = null,
                            tint = HoneyPrimaryVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Packs âges",
                            color = HoneyPrimaryVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Filter by Age Bracket
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Filtrer par tranche d'âge :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val ageOptions = listOf(
                            null to "Tous les âges",
                            "3-5" to "👶 3-5 ans (Tout-petits)",
                            "6-8" to "🎒 6-8 ans (Écoliers)",
                            "8+" to "🌟 8+ ans (Grands)"
                        )
                        items(ageOptions) { (key, label) ->
                            val isSelected = selectedAgeFilter == key
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFFEFF6FF),
                                modifier = Modifier.clickable { selectedAgeFilter = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Filter by Child
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAll = selectedChildFilter == null
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isAll) HoneyPrimary else Color.White,
                            modifier = Modifier.clickable { selectedChildFilter = null }
                        ) {
                            Text(
                                text = "Tous les enfants",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAll) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    items(children) { child ->
                        val isSelected = selectedChildFilter == child.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) HoneyPrimary else Color.White,
                            modifier = Modifier.clickable { selectedChildFilter = child.id }
                        ) {
                            Text(
                                text = "${child.name} (${child.age} ans)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Filter by Category
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAll = selectedCategoryFilter == null
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAll) Color(0xFF1F2937) else Color(0xFFF3F4F6),
                            modifier = Modifier.clickable { selectedCategoryFilter = null }
                        ) {
                            Text(
                                text = "Toutes catégories",
                                fontSize = 11.sp,
                                color = if (isAll) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    items(Category.values()) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cat.badgeColor else Color(0xFFF3F4F6),
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = "${CategoryIconHelper.getEmojiForCategory(cat)} ${cat.labelFr}",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Missions list
            items(filteredMissions, key = { it.id }) { mission ->
                val targetChild = children.firstOrNull { it.id == mission.childId }

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
                        // Category Icon
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(mission.category.badgeColor.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIconByKey(mission.iconKey),
                                contentDescription = null,
                                tint = mission.category.badgeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Mission Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${mission.period.emoji} ${mission.period.labelFr}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = mission.category.badgeColor
                                )
                                if (mission.specificTime != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⏰ ${mission.specificTime}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Text(
                                text = mission.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )

                            if (mission.description.isNotBlank()) {
                                Text(
                                    text = mission.description,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Badges: Points + Age range + Parent validation + Assigned child
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Points
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HoneyLight
                                ) {
                                    Text(
                                        text = "+${mission.points} miel 🍯",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HoneyPrimaryVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Age range pill
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = "🎯 ${mission.ageRangeLabel}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Child assignment
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF3F4F6)
                                ) {
                                    Text(
                                        text = if (targetChild != null) "👤 ${targetChild.name}" else "👥 Tous éligibles",
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Notification / Parental validation flag
                            if (mission.requiresParentValidation) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = HoneyPrimaryVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Validation parentale obligatoire (notif sur téléphone)",
                                        fontSize = 10.sp,
                                        color = HoneyPrimaryVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Edit & Delete actions
                        IconButton(
                            onClick = { missionToEdit = mission },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modifier",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteMission(mission) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button to create mission
        FloatingActionButton(
            onClick = {
                missionToEdit = null
                showCreateDialog = true
            },
            containerColor = HoneyPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_mission_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter une mission")
        }
    }

    // Edit/Create Dialog
    if (showCreateDialog || missionToEdit != null) {
        MissionEditDialog(
            initialMission = missionToEdit,
            children = children,
            onDismiss = {
                showCreateDialog = false
                missionToEdit = null
            },
            onSave = { saved ->
                onSaveMission(saved)
                showCreateDialog = false
                missionToEdit = null
            }
        )
    }

    // Routine Templates Dialog
    if (showTemplatesDialog) {
        RoutineTemplatesDialog(
            children = children,
            onApply = { template, childId ->
                onApplyTemplate(template, childId)
                showTemplatesDialog = false
            },
            onDismiss = { showTemplatesDialog = false }
        )
    }
}

@Composable
fun MissionEditDialog(
    initialMission: Mission?,
    children: List<ChildProfile>,
    onDismiss: () -> Unit,
    onSave: (Mission) -> Unit
) {
    var title by remember { mutableStateOf(initialMission?.title ?: "") }
    var description by remember { mutableStateOf(initialMission?.description ?: "") }
    var category by remember { mutableStateOf(initialMission?.category ?: Category.AUTONOMIE) }
    var points by remember { mutableIntStateOf(initialMission?.points ?: 2) }
    var period by remember { mutableStateOf(initialMission?.period ?: PeriodOfDay.MATIN) }
    var specificTime by remember { mutableStateOf(initialMission?.specificTime ?: "") }
    var targetChildId by remember { mutableStateOf(initialMission?.childId ?: 0L) }
    var requiresValidation by remember { mutableStateOf(initialMission?.requiresParentValidation ?: false) }
    var minAge by remember { mutableIntStateOf(initialMission?.minAge ?: 3) }
    var maxAge by remember { mutableIntStateOf(initialMission?.maxAge ?: 18) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (initialMission == null) "Créer une Mission ✨" else "Modifier la Mission ✏️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de la mission") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Petite consigne / explication") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Age Brackets Section (CRITICAL FEATURE)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tranche d'âge recommandée :", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        // Quick age presets
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val agePresets = listOf(
                                Triple("👶 3-5 ans", 3, 5),
                                Triple("🎒 6-8 ans", 6, 8),
                                Triple("🌟 8+ ans", 8, 18),
                                Triple("🌐 Tous (3-18)", 3, 18)
                            )
                            agePresets.forEach { (label, min, max) ->
                                val isSelected = minAge == min && maxAge == max
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFF3F4F6),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            minAge = min
                                            maxAge = max
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Fine-tune Steppers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Min: $minAge ans", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                                    Row {
                                        IconButton(onClick = { if (minAge > 2) minAge-- }, modifier = Modifier.size(28.dp)) {
                                            Text("➖", fontSize = 12.sp)
                                        }
                                        IconButton(onClick = { if (minAge < maxAge) minAge++ }, modifier = Modifier.size(28.dp)) {
                                            Text("➕", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Max: ${if (maxAge >= 18) "18+" else "$maxAge ans"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                                    Row {
                                        IconButton(onClick = { if (maxAge > minAge) maxAge-- }, modifier = Modifier.size(28.dp)) {
                                            Text("➖", fontSize = 12.sp)
                                        }
                                        IconButton(onClick = { if (maxAge < 18) maxAge++ }, modifier = Modifier.size(28.dp)) {
                                            Text("➕", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "💡 Les enfants de moins de $minAge ans ne verront pas cette mission.",
                            fontSize = 11.sp,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                item {
                    Text("Catégorie :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(Category.values()) { cat ->
                            val isSelected = category == cat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) cat.badgeColor else Color(0xFFF3F4F6),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = "${CategoryIconHelper.getEmojiForCategory(cat)} ${cat.labelFr}",
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Points de miel :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (points > 1) points-- }) {
                                Text("➖", fontSize = 16.sp)
                            }
                            Text(
                                text = "$points 🍯",
                                fontWeight = FontWeight.ExtraBold,
                                color = HoneyPrimaryVariant,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { if (points < 10) points++ }) {
                                Text("➕", fontSize = 16.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Moment de la journée :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PeriodOfDay.values().forEach { p ->
                            val isSelected = period == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) HoneyPrimary else Color(0xFFF3F4F6),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { period = p }
                            ) {
                                Text(
                                    text = p.emoji + "\n" + p.labelFr,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = specificTime,
                        onValueChange = { specificTime = it },
                        label = { Text("Heure indicative (ex: 07:30 - optionnel)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Pour qui :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (targetChildId == 0L) HoneyPrimary else Color(0xFFF3F4F6),
                            modifier = Modifier.clickable { targetChildId = 0L }
                        ) {
                            Text(
                                text = "Tous les enfants",
                                fontSize = 12.sp,
                                color = if (targetChildId == 0L) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        children.forEach { ch ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (targetChildId == ch.id) HoneyPrimary else Color(0xFFF3F4F6),
                                modifier = Modifier.clickable { targetChildId = ch.id }
                            ) {
                                Text(
                                    text = "${ch.name} (${ch.age}a)",
                                    fontSize = 12.sp,
                                    color = if (targetChildId == ch.id) Color.White else Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Parental Validation on Phone Switch
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (requiresValidation) Color(0xFFFEF3C7) else Color(0xFFF9FAFB),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🛡️ Exiger validation parentale", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Notification sur votre téléphone à la validation, avec accord requis", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = requiresValidation,
                                onCheckedChange = { requiresValidation = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Annuler", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val mission = Mission(
                                        id = initialMission?.id ?: 0L,
                                        childId = targetChildId,
                                        title = title.trim(),
                                        description = description.trim(),
                                        category = category,
                                        points = points,
                                        period = period,
                                        specificTime = specificTime.ifBlank { null },
                                        daysOfWeek = initialMission?.daysOfWeek ?: "1,2,3,4,5,6,7",
                                        iconKey = initialMission?.iconKey ?: "star",
                                        requiresParentValidation = requiresValidation,
                                        minAge = minAge,
                                        maxAge = maxAge
                                    )
                                    onSave(mission)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary)
                        ) {
                            Text("Enregistrer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineTemplatesDialog(
    children: List<ChildProfile>,
    onApply: (RoutineTemplate, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedChildId by remember { mutableStateOf(0L) } // 0 = all

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Bibliothèque de Routines par Âge 📚",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Sélectionnez un pack adapté à l'âge de votre enfant pour l'ajouter en 1 clic :",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Child selection
                item {
                    Text("Appliquer à :", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedChildId == 0L) HoneyPrimary else Color(0xFFF3F4F6),
                            modifier = Modifier.clickable { selectedChildId = 0L }
                        ) {
                            Text(
                                text = "Tous les enfants",
                                fontSize = 11.sp,
                                color = if (selectedChildId == 0L) Color.White else Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        children.forEach { ch ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedChildId == ch.id) HoneyPrimary else Color(0xFFF3F4F6),
                                modifier = Modifier.clickable { selectedChildId = ch.id }
                            ) {
                                Text(
                                    text = "${ch.name} (${ch.age}a)",
                                    fontSize = 11.sp,
                                    color = if (selectedChildId == ch.id) Color.White else Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Templates list with Age Bracket highlighted
                items(RoutineTemplate.ALL_TEMPLATES) { template ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = template.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = template.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1F2937)
                                        )
                                        // Age bracket label
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFEFF6FF),
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = template.ageBracketLabel,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1D4ED8),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = { onApply(template, selectedChildId) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary)
                                ) {
                                    Text("Ajouter", fontSize = 11.sp, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = template.description,
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Missions : " + template.items.joinToString(" • ") { it.title },
                                fontSize = 10.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Fermer", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
