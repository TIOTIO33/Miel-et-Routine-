package com.example.ui.screens.parent

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ParentSettings
import com.example.ui.notifications.ParentNotificationHelper
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.KidPillGreen
import com.example.ui.theme.SageGreenLight

@Composable
fun ParentSettingsScreen(
    currentSettings: ParentSettings?,
    hiveCode: String? = null,
    isCloudConnected: Boolean = false,
    onOpenFamilyLink: () -> Unit = {},
    onSaveSettings: (ParentSettings) -> Unit,
    onTestNotification: () -> Unit,
    onBackToKids: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = currentSettings ?: ParentSettings()
    val context = LocalContext.current

    var familyName by remember(settings) { mutableStateOf(settings.familyName) }
    var morningTime by remember(settings) { mutableStateOf(settings.morningReminderTime) }
    var eveningTime by remember(settings) { mutableStateOf(settings.eveningReminderTime) }
    var notificationsEnabled by remember(settings) { mutableStateOf(settings.notificationsEnabled) }
    var notifyOnValidation by remember(settings) { mutableStateOf(settings.notifyOnValidation) }
    var requireValidationUnderAge7 by remember(settings) { mutableStateOf(settings.requireValidationUnderAge7) }
    var defaultValidation by remember(settings) { mutableStateOf(settings.defaultRequireParentValidation) }
    var soundEffects by remember(settings) { mutableStateOf(settings.soundEffectsEnabled) }
    var reducedAnimations by remember(settings) { mutableStateOf(settings.reducedAnimations) }

    var showPinChangeDialog by remember { mutableStateOf(false) }

    var hasNotificationPerm by remember {
        mutableStateOf(ParentNotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPerm = isGranted
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
                text = "Paramètres & Sécurité ⚙️",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "Gérez le contrôle parental, les alertes notifications et les règles par âge",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Parent Notification & Control Center (FEATURE SPECIFIC)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = HoneyPrimaryVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Contrôle Parental & Notifications Téléphone 📱",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Alertes directes lors des validations des enfants",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Notification Permission check on Android 13+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPerm) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Permission notification requise", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFDC2626))
                                    Text("Autorisez l'application à afficher des alertes sur ce téléphone.", fontSize = 11.sp, color = Color(0xFF991B1B))
                                }
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Autoriser", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Switch 1: Notifications à chaque validation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Alerte téléphone à chaque validation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Envoie une notification push sonore/visuelle dès qu'un enfant valide une routine.", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = notifyOnValidation,
                            onCheckedChange = { notifyOnValidation = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }

                    // Switch 2: Validation obligatoire pour < 7 ans (Léo 4 ans)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Validation obligatoire pour les tout-petits (< 7 ans)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Les enfants de 3 à 6 ans doivent avoir votre accord pour recevoir leurs points de miel.", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = requireValidationUnderAge7,
                            onCheckedChange = { requireValidationUnderAge7 = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }

                    // Switch 3: Validation obligatoire pour TOUTES les routines
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Exiger validation pour toutes les routines", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Même pour les plus grands, aucune routine n'est créditée sans votre accord.", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = defaultValidation,
                            onCheckedChange = { defaultValidation = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }

                    // Button: Test notification
                    OutlinedButton(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = HoneyPrimaryVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔔 Tester la notification sur ce téléphone",
                            fontWeight = FontWeight.Bold,
                            color = HoneyPrimaryVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Multi-Device & Cross-Phone Hive Sync Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF60A5FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFEFF6FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔗", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Liaison Téléphones Parent / Enfant",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (isCloudConnected && !hiveCode.isNullOrBlank()) "Ruche active : $hiveCode 🟢" else "Non synchronisé en ligne ⚪",
                                    fontSize = 11.sp,
                                    color = if (isCloudConnected) Color(0xFF16A34A) else Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Synchronisez le téléphone de l'enfant avec celui du parent en temps réel via un Code Ruche sécurisé. Les alertes et approbations sont transmises instantanément d'un téléphone à l'autre.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = onOpenFamilyLink,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isCloudConnected) "Gérer la Ruche ($hiveCode)" else "Configurer la Ruche & Lier Téléphones 📱",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Family Identity
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Identité de la Famille",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = familyName,
                        onValueChange = { familyName = it },
                        label = { Text("Nom de la famille ou de la ruche") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Parental Lock PIN
        item {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = HoneyPrimaryVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Code PIN Parental",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Code actuel : ${settings.pinCode}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Button(
                        onClick = { showPinChangeDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary)
                    ) {
                        Text("Modifier", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Daily Reminders
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SageGreenLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFF047857),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Rappels Quotidiens",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Rappels matin et soir actifs", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Rappels bienveillants pour lancer la journée et le coucher", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = morningTime,
                            onValueChange = { morningTime = it },
                            label = { Text("Rappel matin") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = eveningTime,
                            onValueChange = { eveningTime = it },
                            label = { Text("Rappel soir") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Accessibility & Sound Effects
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Options & Accessibilité",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sons et effets ludiques", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Bruits de miel et célébrations", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = soundEffects,
                            onCheckedChange = { soundEffects = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Animations allégées", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Pour les enfants sensibles aux stimulations", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = reducedAnimations,
                            onCheckedChange = { reducedAnimations = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = HoneyPrimary)
                        )
                    }
                }
            }
        }

        // Privacy & RGPD Note
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Confidentialité & RGPD Enfants",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "Zéro publicité, zéro revente de données. Tout reste privé et sécurisé sur votre appareil.",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Save Settings Action
        item {
            Button(
                onClick = {
                    onSaveSettings(
                        settings.copy(
                            familyName = familyName.trim(),
                            morningReminderTime = morningTime.trim(),
                            eveningReminderTime = eveningTime.trim(),
                            notificationsEnabled = notificationsEnabled,
                            notifyOnValidation = notifyOnValidation,
                            requireValidationUnderAge7 = requireValidationUnderAge7,
                            defaultRequireParentValidation = defaultValidation,
                            soundEffectsEnabled = soundEffects,
                            reducedAnimations = reducedAnimations
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Enregistrer les modifications 💾",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }

        // Return to Kids Button
        item {
            Button(
                onClick = onBackToKids,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "🐝 Revenir à l'Espace Enfants",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showPinChangeDialog) {
        PinChangeDialog(
            currentPin = settings.pinCode,
            onDismiss = { showPinChangeDialog = false },
            onSave = { newPin ->
                onSaveSettings(settings.copy(pinCode = newPin))
                showPinChangeDialog = false
            }
        )
    }
}

@Composable
fun PinChangeDialog(
    currentPin: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Changer le code PIN parental 🔒",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Text(
                    text = "Entrez un nouveau code à 4 chiffres :",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            newPin = it
                        }
                    },
                    label = { Text("Nouveau code PIN (4 chiffres)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

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
                            if (newPin.length == 4) {
                                onSave(newPin)
                            }
                        },
                        enabled = newPin.length == 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary)
                    ) {
                        Text("Changer", color = Color.White)
                    }
                }
            }
        }
    }
}
