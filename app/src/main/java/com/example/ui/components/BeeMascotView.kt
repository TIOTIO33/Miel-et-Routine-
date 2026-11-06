package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimaryVariant
import kotlin.math.roundToInt

@Composable
fun BeeMascotView(
    modifier: Modifier = Modifier,
    hat: String = "none",
    colorTheme: String = "honey_gold",
    speechText: String? = null,
    size: Int = 120,
    showSpeechBubble: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bee_floating")
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    val wingRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing"
    )

    val cheerfulQuotes = remember {
        listOf(
            "Tu butines comme un chef ! 🐝",
            "Super journée pour grandir ! ☀️",
            "Prêt pour de nouvelles missions ? ✨",
            "Regarde tout ce bon miel ! 🍯",
            "Chaque petite tâche te rend plus autonome ! 🌟"
        )
    }

    var quoteIndex by remember { mutableIntStateOf(0) }
    val displaySpeech = speechText ?: cheerfulQuotes[quoteIndex % cheerfulQuotes.size]

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showSpeechBubble) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = HoneyLight,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        quoteIndex++
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🐝 Bizz :",
                        fontWeight = FontWeight.Bold,
                        color = HoneyPrimaryVariant,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = displaySpeech,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF451A03),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(0, bobbingOffset.roundToInt()) }
                .size(size.dp)
                .clickable {
                    quoteIndex++
                    onClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            // Main Bee Mascot Asset
            Image(
                painter = painterResource(id = R.drawable.ic_bee_mascot),
                contentDescription = "Mascotte Bizz l'Abeille",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size((size * 0.95).dp)
                    .clip(CircleShape)
            )

            // Accessory Overlays
            when (hat) {
                "crown" -> {
                    Text(
                        text = "👑",
                        fontSize = (size * 0.32).sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-size * 0.12).dp)
                    )
                }
                "party" -> {
                    Text(
                        text = "🥳",
                        fontSize = (size * 0.30).sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-size * 0.10).dp)
                    )
                }
                "sunglasses" -> {
                    Text(
                        text = "🕶️",
                        fontSize = (size * 0.28).sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-size * 0.04).dp)
                    )
                }
                "chef" -> {
                    Text(
                        text = "👨‍🍳",
                        fontSize = (size * 0.32).sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-size * 0.12).dp)
                    )
                }
            }
        }
    }
}
