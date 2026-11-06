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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChildProfile
import com.example.data.model.Reward
import com.example.data.model.RewardRedemption
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.HoneySurfaceVariant
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight

@Composable
fun KidsRewardsScreen(
    currentChild: ChildProfile?,
    rewards: List<Reward>,
    myRedemptions: List<RewardRedemption>,
    onRequestReward: (Reward) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPoints = currentChild?.currentPoints ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Honey Balance Hero Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = HoneyLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Boutique Magique 🎁",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HoneyPrimaryVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Échange tes gouttes de miel contre des moments géniaux !",
                            fontSize = 13.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🍯", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$childPoints",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = HoneyPrimaryVariant
                            )
                        }
                    }
                }
            }
        }

        // Section: Pending or Recent Requests
        val myRequests = myRedemptions.filter { it.childId == currentChild?.id }
        if (myRequests.isNotEmpty()) {
            item {
                Text(
                    text = "Mes demandes en cours ✨",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            items(myRequests.take(4)) { req ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (req.status) {
                        "PENDING" -> Color(0xFFFFFBEB)
                        "APPROVED", "DELIVERED" -> SageGreenLight
                        else -> Color(0xFFF3F4F6)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (req.status == "PENDING") Icons.Default.HourglassTop else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (req.status == "PENDING") HoneyPrimaryVariant else SageGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = req.rewardTitle,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = when (req.status) {
                                        "PENDING" -> "En attente de validation par papa/maman ⏳"
                                        "APPROVED" -> "Validée ! Prépare-toi 🎉"
                                        "DELIVERED" -> "Récompense savourée ! ✨"
                                        else -> "Demande refusée (points rendus)"
                                    },
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        Text(
                            text = "-${req.costPoints} 🍯",
                            fontWeight = FontWeight.Bold,
                            color = HoneyPrimaryVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section: Available Rewards
        item {
            Text(
                text = "Toutes les récompenses disponibles :",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        items(rewards) { reward ->
            val canAfford = childPoints >= reward.costPoints
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                if (canAfford) HoneyLight else Color(0xFFF3F4F6),
                                CircleShape
                            ),
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
                            fontSize = 26.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reward.description,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Coût : ${reward.costPoints} gouttes de miel 🍯",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HoneyPrimaryVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onRequestReward(reward) },
                        enabled = canAfford,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoneyPrimary,
                            disabledContainerColor = Color(0xFFE5E7EB)
                        )
                    ) {
                        Text(
                            text = if (canAfford) "Choisir" else "Bloqué",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (canAfford) Color.White else Color.Gray
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
