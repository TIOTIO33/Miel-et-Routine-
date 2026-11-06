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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChildProfile
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight

@Composable
fun ParentChildrenScreen(
    children: List<ChildProfile>,
    onSaveChild: (ChildProfile) -> Unit,
    onDeleteChild: (ChildProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var childToEdit by remember { mutableStateOf<ChildProfile?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Profils des Enfants 👦👧",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Gérez les profils, âges et modes de validation de chaque enfant",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            items(children, key = { it.id }) { child ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(HoneyLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (child.name.startsWith("L", ignoreCase = true)) "👦" else "👧",
                                        fontSize = 26.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = child.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "${child.age} ans • ${child.levelTitle}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = { childToEdit = child }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifier",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (children.size > 1) {
                                    IconButton(onClick = { onDeleteChild(child) }) {
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFFBEB),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Solde miel", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = "${child.currentPoints} 🍯",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HoneyPrimaryVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SageGreenLight,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Missions réussies", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = "${child.completedMissionsCount} ✅",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreenDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (child.requiresParentValidation) {
                                "🛡️ Mode validation parentale activé (papa/maman valide les tâches)"
                            } else {
                                "⚡ Mode autonome : validation automatique directe"
                            },
                            fontSize = 11.sp,
                            color = if (child.requiresParentValidation) HoneyPrimaryVariant else SageGreenDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        FloatingActionButton(
            onClick = {
                childToEdit = null
                showAddDialog = true
            },
            containerColor = HoneyPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un enfant")
        }
    }

    if (showAddDialog || childToEdit != null) {
        ChildEditDialog(
            initialChild = childToEdit,
            onDismiss = {
                showAddDialog = false
                childToEdit = null
            },
            onSave = { saved ->
                onSaveChild(saved)
                showAddDialog = false
                childToEdit = null
            }
        )
    }
}

@Composable
fun ChildEditDialog(
    initialChild: ChildProfile?,
    onDismiss: () -> Unit,
    onSave: (ChildProfile) -> Unit
) {
    var name by remember { mutableStateOf(initialChild?.name ?: "") }
    var age by remember { mutableIntStateOf(initialChild?.age ?: 5) }
    var requiresValidation by remember { mutableStateOf(initialChild?.requiresParentValidation ?: (age < 7)) }

    val ageStageDescription = when {
        age <= 5 -> "👶 Tout-petit / Maternelle (3-5 ans) : routines douces (doudou, brossage guidé, scratchs, ranger 3 jouets)"
        age in 6..8 -> "🎒 Écolier (6-8 ans) : autonomie (lit, cartable, devoirs du soir, débarrasser son assiette)"
        age in 9..12 -> "🌟 Grand autonome (9-12 ans) : vraies responsabilités (lave-vaisselle, chambre à fond, sac de sport)"
        else -> "🧑 Ado (13+ ans) : gestion autonome et entraide dans la maison"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (initialChild == null) "Ajouter un Enfant 👶" else "Modifier le Profil de ${initialChild.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Prénom de l'enfant") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Age Stepper and Quick Presets
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Âge de l'enfant :", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (age > 2) {
                                    age--
                                    if (initialChild == null && age < 7) requiresValidation = true
                                }
                            }) {
                                Text("➖", fontSize = 16.sp)
                            }
                            Text(
                                text = "$age ans",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = HoneyPrimaryVariant
                            )
                            IconButton(onClick = {
                                if (age < 17) {
                                    age++
                                    if (initialChild == null && age >= 8) requiresValidation = false
                                }
                            }) {
                                Text("➕", fontSize = 16.sp)
                            }
                        }
                    }

                    // Quick Age Presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(3, 4, 6, 8, 10).forEach { presetAge ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (age == presetAge) HoneyPrimary else Color(0xFFF3F4F6),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        age = presetAge
                                        if (initialChild == null) requiresValidation = (presetAge < 7)
                                    }
                            ) {
                                Text(
                                    text = "$presetAge ans",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (age == presetAge) Color.White else Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Age Guidance Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "🎯 Adaptation automatique des routines :",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ageStageDescription,
                            fontSize = 11.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 15.sp
                        )
                    }
                }

                // Parent Validation / Notification Switch
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (requiresValidation) Color(0xFFFEF3C7) else Color(0xFFF9FAFB),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (requiresValidation) Color(0xFFFDE68A) else Color(0xFFE5E7EB)
                    ),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🛡️ Contrôle parental actif",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1F2937)
                                )
                            }
                            Text(
                                text = "Envoie une notif sur votre téléphone et exige votre accord pour valider les tâches.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp
                            )
                        }
                        Switch(
                            checked = requiresValidation,
                            onCheckedChange = { requiresValidation = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }
                }

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
                            if (name.isNotBlank()) {
                                onSave(
                                    ChildProfile(
                                        id = initialChild?.id ?: 0L,
                                        name = name.trim(),
                                        age = age,
                                        avatarKey = initialChild?.avatarKey ?: "bee_happy",
                                        beeHat = initialChild?.beeHat ?: "none",
                                        beeColor = initialChild?.beeColor ?: "honey_gold",
                                        currentPoints = initialChild?.currentPoints ?: 10,
                                        totalPointsEarned = initialChild?.totalPointsEarned ?: 10,
                                        completedMissionsCount = initialChild?.completedMissionsCount ?: 0,
                                        level = initialChild?.level ?: 1,
                                        requiresParentValidation = requiresValidation
                                    )
                                )
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
