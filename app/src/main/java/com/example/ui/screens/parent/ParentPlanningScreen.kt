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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.PeriodOfDay
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import java.util.Calendar

data class WeekDayInfo(val dayNumber: Int, val shortLabel: String, val fullLabel: String)

@Composable
fun ParentPlanningScreen(
    children: List<ChildProfile>,
    missions: List<Mission>,
    modifier: Modifier = Modifier
) {
    var selectedDayNumber by remember {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_WEEK)
        val initial = if (d == Calendar.SUNDAY) 7 else d - 1
        mutableIntStateOf(initial)
    }

    var selectedChildId by remember { mutableStateOf<Long?>(null) } // null = all

    val daysOfWeek = listOf(
        WeekDayInfo(1, "Lun", "Lundi"),
        WeekDayInfo(2, "Mar", "Mardi"),
        WeekDayInfo(3, "Mer", "Mercredi"),
        WeekDayInfo(4, "Jeu", "Jeudi"),
        WeekDayInfo(5, "Ven", "Vendredi"),
        WeekDayInfo(6, "Sam", "Samedi"),
        WeekDayInfo(7, "Dim", "Dimanche")
    )

    val currentDayInfo = daysOfWeek.firstOrNull { it.dayNumber == selectedDayNumber } ?: daysOfWeek[0]

    val dayMissions = missions.filter { m ->
        val child = children.firstOrNull { it.id == selectedChildId }
        val childMatch = if (selectedChildId == null) {
            true
        } else {
            if (m.childId == 0L) {
                child?.let { m.isAppropriateForAge(it.age) } ?: true
            } else {
                m.childId == selectedChildId
            }
        }
        val dayMatch = m.appliesToDay(selectedDayNumber)
        childMatch && dayMatch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Planning Hebdomadaire 📅",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "Consultez l'emploi du temps des routines du lundi au dimanche",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Days selector bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    val isSelected = day.dayNumber == selectedDayNumber
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedDayNumber = day.dayNumber }
                            .padding(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) HoneyPrimary else Color.White,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = day.shortLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF4B5563)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Child filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedChildId == null) HoneyPrimary else Color.White,
                    modifier = Modifier.clickable { selectedChildId = null }
                ) {
                    Text(
                        text = "Tous les enfants",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedChildId == null) Color.White else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                children.forEach { ch ->
                    val isSelected = selectedChildId == ch.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) HoneyPrimary else Color.White,
                        modifier = Modifier.clickable { selectedChildId = ch.id }
                    ) {
                        Text(
                            text = ch.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Day Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${currentDayInfo.fullLabel} :",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "${dayMissions.size} missions programmées ce jour-là",
                            fontSize = 12.sp,
                            color = HoneyPrimaryVariant
                        )
                    }
                    Text(text = "🐝", fontSize = 28.sp)
                }
            }
        }

        // Group by period
        val periods = listOf(PeriodOfDay.MATIN, PeriodOfDay.MIDI, PeriodOfDay.SOIR, PeriodOfDay.JOURNEE)
        periods.forEach { period ->
            val periodMissions = dayMissions.filter { it.period == period }
            if (periodMissions.isNotEmpty()) {
                item {
                    Text(
                        text = "${period.emoji} ${period.labelFr} (${periodMissions.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }

                items(periodMissions, key = { it.id }) { m ->
                    val assigned = children.firstOrNull { it.id == m.childId }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(m.category.badgeColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIconByKey(m.iconKey),
                                    contentDescription = null,
                                    tint = m.category.badgeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = m.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "${CategoryIconHelper.getEmojiForCategory(m.category)} ${m.category.labelFr}" +
                                            (if (m.specificTime != null) " • ⏰ ${m.specificTime}" else "") +
                                            " • 🎯 ${m.ageRangeLabel}" +
                                            " • Pour : ${assigned?.name ?: "Tous éligibles"}" +
                                            (if (m.requiresParentValidation) " • 🛡️ Parent" else ""),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = "+${m.points} 🍯",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HoneyPrimaryVariant
                            )
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
