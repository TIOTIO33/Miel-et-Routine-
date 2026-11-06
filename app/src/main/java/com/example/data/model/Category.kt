package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.KidPillBlue
import com.example.ui.theme.KidPillCoral
import com.example.ui.theme.KidPillGreen
import com.example.ui.theme.KidPillPurple
import com.example.ui.theme.KidPillYellow
import com.example.ui.theme.LavenderPastel
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SoftOrange

enum class Category(
    val labelFr: String,
    val iconName: String,
    val badgeColor: Color
) {
    AUTONOMIE("Autonomie", "star", KidPillYellow),
    HYGIENE("Hygiène", "shower", KidPillBlue),
    HABILLEMENT("Habillage", "checkroom", SoftOrange),
    ECOLE("École", "school", SkyBlueDark),
    DEVOIRS("Devoirs", "menu_book", KidPillPurple),
    RANGEMENT("Rangement", "inventory_2", SageGreenDark),
    BIENVEILLANCE("Bienveillance", "favorite", KidPillCoral),
    SOMMEIL("Sommeil", "bedtime", LavenderPastel),
    REPAS("Repas", "restaurant", HoneyPrimary),
    RESPONSABILITES("Responsabilités", "psychology", KidPillGreen),
    VIE_QUOTIDIENNE("Vie quotidienne", "wb_sunny", KidPillYellow);

    companion object {
        fun fromString(value: String): Category {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                VIE_QUOTIDIENNE
            }
        }
    }
}
