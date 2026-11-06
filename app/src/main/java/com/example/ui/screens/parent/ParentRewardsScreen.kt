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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.Reward
import com.example.data.model.RewardRedemption
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.KidPillGreen
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight

@Composable
fun ParentRewardsScreen(
    rewards: List<Reward>,
    redemptions: List<RewardRedemption>,
    onSaveReward: (Reward) -> Unit,
    onDeleteReward: (Reward) -> Unit,
    onApproveRedemption: (Long) -> Unit,
    onDeliverRedemption: (Long) -> Unit,
    onRejectRedemption: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var rewardToEdit by remember { mutableStateOf<Reward?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Boutique & Récompenses 🎁",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = HoneyPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        val pendingCount = redemptions.count { it.status == "PENDING" }
                        Text(
                            text = if (pendingCount > 0) "Demandes ($pendingCount)" else "Demandes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Catalogue (${rewards.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // Redemptions list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (redemptions.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🎈", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Aucune demande en cours",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "Les enfants peuvent échanger leurs points dans leur espace.",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        items(redemptions, key = { it.id }) { red ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🎁", fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = red.rewardTitle,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color(0xFF1F2937)
                                                )
                                                Text(
                                                    text = "Demandé par ${red.childName} • Coût : ${red.costPoints} miel 🍯",
                                                    fontSize = 12.sp,
                                                    color = HoneyPrimaryVariant
                                                )
                                            }
                                        }

                                        // Status badge
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = when (red.status) {
                                                "PENDING" -> Color(0xFFFFFBEB)
                                                "APPROVED" -> SageGreenLight
                                                "DELIVERED" -> Color(0xFFE0F2FE)
                                                else -> Color(0xFFFEE2E2)
                                            }
                                        ) {
                                            Text(
                                                text = when (red.status) {
                                                    "PENDING" -> "En attente"
                                                    "APPROVED" -> "Accordée"
                                                    "DELIVERED" -> "Livrée / Faite"
                                                    else -> "Refusée"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (red.status) {
                                                    "PENDING" -> HoneyPrimaryVariant
                                                    "APPROVED" -> SageGreenDark
                                                    "DELIVERED" -> Color(0xFF0284C7)
                                                    else -> Color(0xFFDC2626)
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    if (red.status == "PENDING") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { onRejectRedemption(red.id) }) {
                                                Text("Refuser & Rembourser", color = Color(0xFFEF4444), fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { onApproveRedemption(red.id) },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = KidPillGreen)
                                            ) {
                                                Text("Accorder 🎉", fontSize = 12.sp, color = Color.White)
                                            }
                                        }
                                    } else if (red.status == "APPROVED") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Button(
                                                onClick = { onDeliverRedemption(red.id) },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary)
                                            ) {
                                                Text("Marquer comme effectuée / savourée ✅", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Catalogue list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rewards, key = { it.id }) { reward ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
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
                                        .size(46.dp)
                                        .background(HoneyLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (reward.iconKey) {
                                            "cake" -> "🍰"
                                            "movie" -> "🎬"
                                            "menu_book" -> "📖"
                                            "park" -> "🌳"
                                            "sports_esports" -> "🎲"
                                            "restaurant" -> "🍕"
                                            else -> "🎁"
                                        },
                                        fontSize = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reward.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = reward.description,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${reward.costPoints} gouttes de miel 🍯",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HoneyPrimaryVariant
                                    )
                                }

                                IconButton(onClick = { rewardToEdit = reward }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifier",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(onClick = { onDeleteReward(reward) }) {
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
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }

        if (selectedTab == 1) {
            FloatingActionButton(
                onClick = {
                    rewardToEdit = null
                    showAddDialog = true
                },
                containerColor = HoneyPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter récompense")
            }
        }
    }

    if (showAddDialog || rewardToEdit != null) {
        RewardEditDialog(
            initialReward = rewardToEdit,
            onDismiss = {
                showAddDialog = false
                rewardToEdit = null
            },
            onSave = { saved ->
                onSaveReward(saved)
                showAddDialog = false
                rewardToEdit = null
            }
        )
    }
}

@Composable
fun RewardEditDialog(
    initialReward: Reward?,
    onDismiss: () -> Unit,
    onSave: (Reward) -> Unit
) {
    var title by remember { mutableStateOf(initialReward?.title ?: "") }
    var description by remember { mutableStateOf(initialReward?.description ?: "") }
    var costPoints by remember { mutableIntStateOf(initialReward?.costPoints ?: 10) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (initialReward == null) "Nouvelle Récompense 🎁" else "Modifier la Récompense",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de la récompense") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Coût en miel :", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (costPoints > 1) costPoints-- }) {
                            Text("➖", fontSize = 16.sp)
                        }
                        Text(
                            text = "$costPoints 🍯",
                            fontWeight = FontWeight.ExtraBold,
                            color = HoneyPrimaryVariant,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { if (costPoints < 100) costPoints++ }) {
                            Text("➕", fontSize = 16.sp)
                        }
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
                            if (title.isNotBlank()) {
                                onSave(
                                    Reward(
                                        id = initialReward?.id ?: 0L,
                                        title = title.trim(),
                                        description = description.trim(),
                                        costPoints = costPoints,
                                        iconKey = initialReward?.iconKey ?: "card_giftcard"
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enregistrer", color = Color.White)
                    }
                }
            }
        }
    }
}
