package com.example.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.cloud.DeviceRole
import com.example.data.model.ChildProfile
import com.example.ui.theme.HoneyBackground
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.KidPillGreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun FamilyLinkDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    hiveCode: String?,
    deviceRole: DeviceRole,
    dedicatedChildId: Long?,
    isCloudConnected: Boolean,
    currentUser: FirebaseUser?,
    allChildren: List<ChildProfile>,
    familyName: String,
    parentPin: String,
    onSignInWithGoogle: (Activity) -> Unit,
    onSignOut: () -> Unit,
    onCreateHive: (familyName: String, parentPin: String, onDone: (String) -> Unit) -> Unit,
    onJoinHive: (code: String, role: DeviceRole, childId: Long?, onDone: (Boolean) -> Unit) -> Unit,
    onSetDeviceRole: (DeviceRole, Long?) -> Unit,
    onDisconnectHive: () -> Unit,
    onTestNotification: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val activity = context as? Activity
    var selectedTab by remember { mutableStateOf(if (hiveCode != null) 0 else 1) } // 0=Info/Statut, 1=Rejoindre/Créer
    var inputCode by remember { mutableStateOf("") }
    var selectedRoleForJoin by remember { mutableStateOf(DeviceRole.CHILD) }
    var selectedChildForJoin by remember { mutableStateOf(allChildren.firstOrNull()?.id) }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("family_link_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(text = "🐝", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Liaison Téléphone Parent & Enfant",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = if (isCloudConnected) "Connecté en temps réel 🟢" else "Mode local (non lié) ⚪",
                                fontSize = 12.sp,
                                color = if (isCloudConnected) KidPillGreen else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Fermer", color = Color.Gray)
                    }
                }

                // Educational banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quand l'enfant valide une mission sur son téléphone (devoirs, rangement...), le parent reçoit une alerte instantanée sur son propre téléphone et valide à distance !",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 15.sp
                        )
                    }
                }

                // Google Account Section (Required for cloud multi-device sync)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔐", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Compte Cloud Famille",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = currentUser?.email ?: "Non connecté à Google",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if (currentUser != null) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Déconnexion", fontSize = 11.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        activity?.let { onSignInWithGoogle(it) }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Connexion Google", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // If Ruche is active, show the active Hive Code prominently
                if (!hiveCode.isNullOrBlank()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, HoneyPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "VOTRE CODE RUCHE FAMILLE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HoneyPrimaryVariant,
                                letterSpacing = 1.sp
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = hiveCode,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF78350F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Code Ruche", hiveCode))
                                        Toast.makeText(context, "Code copié dans le presse-papier !", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.White, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copier",
                                        tint = HoneyPrimaryVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Entrez ce code sur le téléphone de l'enfant ou du 2ème parent pour les lier instantanément !",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    // Role selector for THIS device
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Rôle de cet appareil :",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1F2937)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Parent Phone
                            RoleSelectionCard(
                                title = "Parent 📱",
                                subtitle = "Reçoit alertes & valide",
                                isSelected = deviceRole == DeviceRole.PARENT,
                                onClick = { onSetDeviceRole(DeviceRole.PARENT, null) },
                                modifier = Modifier.weight(1f)
                            )

                            // Child Phone
                            RoleSelectionCard(
                                title = "Enfant 🧒",
                                subtitle = "Dédié à l'enfant",
                                isSelected = deviceRole == DeviceRole.CHILD,
                                onClick = {
                                    val targetChild = dedicatedChildId ?: allChildren.firstOrNull()?.id
                                    onSetDeviceRole(DeviceRole.CHILD, targetChild)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Shared Device
                            RoleSelectionCard(
                                title = "Partagé 🏠",
                                subtitle = "Famille / Maison",
                                isSelected = deviceRole == DeviceRole.SHARED,
                                onClick = { onSetDeviceRole(DeviceRole.SHARED, null) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // If Child Mode is selected on this phone, pick which child it belongs to
                        if (deviceRole == DeviceRole.CHILD) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Cet appareil est le téléphone personnel de :",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        allChildren.forEach { child ->
                                            val isSelected = (dedicatedChildId ?: allChildren.firstOrNull()?.id) == child.id
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isSelected) HoneyPrimary else Color.White,
                                                modifier = Modifier
                                                    .clickable { onSetDeviceRole(DeviceRole.CHILD, child.id) }
                                            ) {
                                                Text(
                                                    text = "${child.name} (${child.age} ans)",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else Color(0xFF1E293B),
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Test Alert Button & Disconnect
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onTestNotification,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tester Alerte", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onDisconnectHive,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Déconnecter", fontSize = 11.sp)
                        }
                    }
                } else {
                    // Not connected yet: Tab for Create or Join
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF8FAFC),
                        contentColor = HoneyPrimaryVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Créer une Ruche", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Rejoindre avec Code", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    if (selectedTab == 0) {
                        // Create Hive (Parent Phone)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "📱 Étape 1 : Créer votre Ruche sur ce téléphone",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Cette action va générer un Code Ruche (ex: RUCHE-482) et sauvegarder vos enfants et missions en ligne sur le Cloud sécurisé.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Button(
                                onClick = {
                                    isLoading = true
                                    onCreateHive(familyName, parentPin) { code ->
                                        isLoading = false
                                        selectedTab = 0
                                    }
                                },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("create_hive_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Générer mon Code Ruche Familiale", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Join Hive (Child Phone or 2nd Parent)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🧒 Étape 2 : Rejoindre la Ruche d'un parent",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Entrez le Code Ruche affiché sur le téléphone des parents :",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            OutlinedTextField(
                                value = inputCode,
                                onValueChange = { inputCode = it.uppercase() },
                                label = { Text("Code Ruche (ex: RUCHE-482)") },
                                placeholder = { Text("RUCHE-XXX") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("hive_code_input")
                            )

                            // Role for this device
                            Text(
                                text = "Cet appareil est le téléphone de :",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRoleForJoin == DeviceRole.CHILD) HoneyLight else Color(0xFFF1F5F9),
                                    border = if (selectedRoleForJoin == DeviceRole.CHILD) androidx.compose.foundation.BorderStroke(1.5.dp, HoneyPrimary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRoleForJoin = DeviceRole.CHILD }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🧒 Enfant", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Effectue les tâches", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRoleForJoin == DeviceRole.PARENT) HoneyLight else Color(0xFFF1F5F9),
                                    border = if (selectedRoleForJoin == DeviceRole.PARENT) androidx.compose.foundation.BorderStroke(1.5.dp, HoneyPrimary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRoleForJoin = DeviceRole.PARENT }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("📱 2ème Parent", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Supervise & valide", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }

                            if (selectedRoleForJoin == DeviceRole.CHILD && allChildren.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    allChildren.forEach { child ->
                                        val isSel = selectedChildForJoin == child.id
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSel) HoneyPrimary else Color(0xFFF8FAFC),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) HoneyPrimary else Color.LightGray),
                                            modifier = Modifier
                                                .clickable { selectedChildForJoin = child.id }
                                                .weight(1f)
                                        ) {
                                            Text(
                                                text = "${child.name} (${child.age}a)",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) Color.White else Color.Black,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    if (inputCode.isNotBlank()) {
                                        isLoading = true
                                        onJoinHive(inputCode, selectedRoleForJoin, selectedChildForJoin) { success ->
                                            isLoading = false
                                        }
                                    }
                                },
                                enabled = inputCode.isNotBlank() && !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = HoneyPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("join_hive_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Rejoindre la Ruche", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) HoneyLight else Color(0xFFF8FAFC),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, HoneyPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isSelected) HoneyPrimaryVariant else Color(0xFF1F2937)
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
