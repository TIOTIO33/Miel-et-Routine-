package com.example.ui.screens.kids

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.BeeMascotView
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant

data class HatOption(val key: String, val label: String, val emoji: String)
data class ColorOption(val key: String, val label: String, val color: Color)

@Composable
fun KidsBeeCustomizerDialog(
    currentHat: String,
    currentColor: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHat by remember { mutableStateOf(currentHat) }
    var selectedColor by remember { mutableStateOf(currentColor) }

    val hats = listOf(
        HatOption("none", "Naturel", "✨"),
        HatOption("crown", "Couronne", "👑"),
        HatOption("party", "Fête", "🥳"),
        HatOption("sunglasses", "Lunettes", "🕶️"),
        HatOption("chef", "Chef Cuisinier", "👨‍🍳")
    )

    val colors = listOf(
        ColorOption("honey_gold", "Miel Doré", Color(0xFFF59E0B)),
        ColorOption("mint_sage", "Menthe Sauge", Color(0xFF10B981)),
        ColorOption("sky_blue", "Bleu Ciel", Color(0xFF38BDF8)),
        ColorOption("coral_rose", "Rose Corail", Color(0xFFF43F5E))
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personnalise ton Abeille ! 🐝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Live Preview
                BeeMascotView(
                    hat = selectedHat,
                    colorTheme = selectedColor,
                    size = 110,
                    showSpeechBubble = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hats selection
                Text(
                    text = "Choisis un accessoire :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(hats) { hat ->
                        val isSelected = hat.key == selectedHat
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) HoneyLight else Color(0xFFF3F4F6))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) HoneyPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedHat = hat.key }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = hat.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = hat.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) HoneyPrimaryVariant else Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Colors selection
                Text(
                    text = "Ambiance de la ruche :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { col ->
                        val isSelected = col.key == selectedColor
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedColor = col.key }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(col.color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color(0xFF1F2937) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = col.label,
                                fontSize = 10.sp,
                                color = if (isSelected) HoneyPrimaryVariant else Color.Gray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSave(selectedHat, selectedColor)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Valider mon look ✨",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
