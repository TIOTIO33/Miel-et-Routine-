package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.FamilyLinkDialog
import com.example.ui.components.ParentPinDialog
import com.example.ui.screens.kids.KidsBadgesScreen
import com.example.ui.screens.kids.KidsBeeCustomizerDialog
import com.example.ui.screens.kids.KidsHomeScreen
import com.example.ui.screens.kids.KidsRewardsScreen
import com.example.ui.screens.parent.ParentChildrenScreen
import com.example.ui.screens.parent.ParentDashboardScreen
import com.example.ui.screens.parent.ParentMissionsScreen
import com.example.ui.screens.parent.ParentPlanningScreen
import com.example.ui.screens.parent.ParentRewardsScreen
import com.example.ui.screens.parent.ParentSettingsScreen
import com.example.ui.theme.HoneyBackground
import com.example.ui.theme.HoneyLight
import com.example.ui.theme.HoneyPrimary
import com.example.ui.theme.HoneyPrimaryVariant
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight
import com.example.ui.viewmodel.RoutineViewModel
import kotlinx.coroutines.launch

enum class KidsNavTab(val label: String) {
    MISSIONS("Missions"),
    REWARDS("Boutique"),
    BADGES("Trophées")
}

enum class ParentNavTab(val label: String) {
    DASHBOARD("Tableau"),
    MISSIONS("Missions"),
    PLANNING("Planning"),
    REWARDS("Cadeaux"),
    CHILDREN("Enfants"),
    SETTINGS("Réglages")
}

@Composable
fun MainAppScreen(
    initialParentTab: String? = null,
    viewModel: RoutineViewModel = viewModel()
) {
    val children by viewModel.allChildren.collectAsStateWithLifecycle()
    val currentChild by viewModel.currentChild.collectAsStateWithLifecycle()
    val missions by viewModel.allMissions.collectAsStateWithLifecycle()
    val completions by viewModel.completionsForSelectedDate.collectAsStateWithLifecycle()
    val activeRewards by viewModel.activeRewards.collectAsStateWithLifecycle()
    val allRewards by viewModel.allRewards.collectAsStateWithLifecycle()
    val allRedemptions by viewModel.allRedemptions.collectAsStateWithLifecycle()
    val pendingRedemptions by viewModel.pendingRedemptions.collectAsStateWithLifecycle()
    val pendingValidations by viewModel.pendingValidations.collectAsStateWithLifecycle()
    val allCompletions by viewModel.allCompletions.collectAsStateWithLifecycle()
    val allBadges by viewModel.allBadges.collectAsStateWithLifecycle()
    val currentChildBadges by viewModel.currentChildBadges.collectAsStateWithLifecycle()
    val parentSettings by viewModel.parentSettings.collectAsStateWithLifecycle()
    val celebrationEvent by viewModel.celebrationEvent.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()

    val hiveCode by viewModel.hiveCode.collectAsStateWithLifecycle()
    val deviceRole by viewModel.deviceRole.collectAsStateWithLifecycle()
    val dedicatedChildId by viewModel.dedicatedChildId.collectAsStateWithLifecycle()
    val isCloudConnected by viewModel.isCloudConnected.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var isParentMode by remember { mutableStateOf(initialParentTab != null) }
    var kidsTab by remember { mutableStateOf(KidsNavTab.MISSIONS) }
    var parentTab by remember {
        mutableStateOf(
            if (initialParentTab == "REWARDS") ParentNavTab.REWARDS else ParentNavTab.DASHBOARD
        )
    }

    var showPinDialog by remember { mutableStateOf(false) }
    var showBeeCustomizer by remember { mutableStateOf(false) }
    var showFamilyLinkDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(snackMessage) {
        snackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackMessage()
        }
    }

    // Back button handling
    BackHandler(enabled = isParentMode) {
        if (parentTab != ParentNavTab.DASHBOARD) {
            parentTab = ParentNavTab.DASHBOARD
        } else {
            isParentMode = false
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = HoneyBackground,
        topBar = {
            if (isParentMode) {
                // Parent Space Header bar
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFFEF3C7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = HoneyPrimaryVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = parentSettings?.familyName ?: "Famille Abeille",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Espace Contrôle Parental",
                                    fontSize = 11.sp,
                                    color = HoneyPrimaryVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Cloud Multi-Phone sync chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCloudConnected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { showFamilyLinkDialog = true }
                                    .testTag("parent_topbar_sync_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(text = if (isCloudConnected) "🟢" else "⚪", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (!hiveCode.isNullOrBlank()) hiveCode!! else "Lier tél.",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isCloudConnected) Color(0xFF15803D) else Color.DarkGray
                                    )
                                }
                            }

                            // Switch to Kids Mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFFBEB),
                                modifier = Modifier
                                    .clickable { isParentMode = false }
                                    .testTag("exit_parent_mode_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "🐝", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Espace Enfant",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HoneyPrimaryVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!isParentMode) {
                // Kids Space Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = kidsTab == KidsNavTab.MISSIONS,
                        onClick = { kidsTab = KidsNavTab.MISSIONS },
                        icon = {
                            Text(text = "🎯", fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                text = "Missions",
                                fontWeight = if (kidsTab == KidsNavTab.MISSIONS) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        ),
                        modifier = Modifier.testTag("kids_tab_missions")
                    )

                    NavigationBarItem(
                        selected = kidsTab == KidsNavTab.REWARDS,
                        onClick = { kidsTab = KidsNavTab.REWARDS },
                        icon = {
                            Text(text = "🎁", fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                text = "Boutique",
                                fontWeight = if (kidsTab == KidsNavTab.REWARDS) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        ),
                        modifier = Modifier.testTag("kids_tab_rewards")
                    )

                    NavigationBarItem(
                        selected = kidsTab == KidsNavTab.BADGES,
                        onClick = { kidsTab = KidsNavTab.BADGES },
                        icon = {
                            Text(text = "🏆", fontSize = 20.sp)
                        },
                        label = {
                            Text(
                                text = "Trophées",
                                fontWeight = if (kidsTab == KidsNavTab.BADGES) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        ),
                        modifier = Modifier.testTag("kids_tab_badges")
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { showPinDialog = true },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Espace Parents",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Parents",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight
                        ),
                        modifier = Modifier.testTag("kids_tab_parent_gate")
                    )
                }
            } else {
                // Parent Space Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.DASHBOARD,
                        onClick = { parentTab = ParentNavTab.DASHBOARD },
                        icon = {
                            Icon(
                                imageVector = if (parentTab == ParentNavTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Tableau",
                                tint = if (parentTab == ParentNavTab.DASHBOARD) HoneyPrimaryVariant else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Tableau", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )

                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.MISSIONS,
                        onClick = { parentTab = ParentNavTab.MISSIONS },
                        icon = {
                            Icon(
                                imageVector = if (parentTab == ParentNavTab.MISSIONS) Icons.Filled.FormatListBulleted else Icons.Outlined.FormatListBulleted,
                                contentDescription = "Missions",
                                tint = if (parentTab == ParentNavTab.MISSIONS) HoneyPrimaryVariant else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Missions", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )

                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.PLANNING,
                        onClick = { parentTab = ParentNavTab.PLANNING },
                        icon = {
                            Icon(
                                imageVector = if (parentTab == ParentNavTab.PLANNING) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                contentDescription = "Planning",
                                tint = if (parentTab == ParentNavTab.PLANNING) HoneyPrimaryVariant else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Planning", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )

                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.REWARDS,
                        onClick = { parentTab = ParentNavTab.REWARDS },
                        icon = {
                            Text(text = "🎁", fontSize = 18.sp)
                        },
                        label = { Text("Cadeaux", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )

                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.CHILDREN,
                        onClick = { parentTab = ParentNavTab.CHILDREN },
                        icon = {
                            Text(text = "👦👧", fontSize = 16.sp)
                        },
                        label = { Text("Enfants", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )

                    NavigationBarItem(
                        selected = parentTab == ParentNavTab.SETTINGS,
                        onClick = { parentTab = ParentNavTab.SETTINGS },
                        icon = {
                            Icon(
                                imageVector = if (parentTab == ParentNavTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Réglages",
                                tint = if (parentTab == ParentNavTab.SETTINGS) HoneyPrimaryVariant else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text("Réglages", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = HoneyLight,
                            selectedTextColor = HoneyPrimaryVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isParentMode) {
                when (kidsTab) {
                    KidsNavTab.MISSIONS -> {
                        KidsHomeScreen(
                            currentChild = currentChild,
                            allChildren = children,
                            missions = missions,
                            completions = completions,
                            hiveCode = hiveCode,
                            isCloudConnected = isCloudConnected,
                            onOpenFamilyLink = { showFamilyLinkDialog = true },
                            onSelectChild = { childId -> viewModel.selectChild(childId) },
                            onToggleMission = { mission, child ->
                                viewModel.toggleMission(mission, child, viewModel.selectedDateStr.value)
                            },
                            onOpenBeeCustomizer = { showBeeCustomizer = true },
                            onOpenParentSpace = { showPinDialog = true }
                        )
                    }
                    KidsNavTab.REWARDS -> {
                        KidsRewardsScreen(
                            currentChild = currentChild,
                            rewards = activeRewards,
                            myRedemptions = allRedemptions,
                            onRequestReward = { reward ->
                                currentChild?.let { ch ->
                                    viewModel.requestReward(reward, ch)
                                }
                            }
                        )
                    }
                    KidsNavTab.BADGES -> {
                        KidsBadgesScreen(
                            currentChild = currentChild,
                            allBadges = allBadges,
                            unlockedBadges = currentChildBadges
                        )
                    }
                }
            } else {
                when (parentTab) {
                    ParentNavTab.DASHBOARD -> {
                        ParentDashboardScreen(
                            children = children,
                            missions = missions,
                            todayCompletions = completions,
                            pendingValidations = pendingValidations,
                            allCompletions = allCompletions,
                            pendingRedemptions = pendingRedemptions,
                            onApproveValidation = { comp -> viewModel.approveValidation(comp) },
                            onApproveAllValidations = { viewModel.approveAllPendingValidations() },
                            onRejectValidation = { comp -> viewModel.rejectValidation(comp) },
                            onApproveRedemption = { id -> viewModel.approveRedemption(id) },
                            onRejectRedemption = { id -> viewModel.rejectRedemption(id) },
                            onNavigateToMissions = { parentTab = ParentNavTab.MISSIONS },
                            onNavigateToRewards = { parentTab = ParentNavTab.REWARDS }
                        )
                    }
                    ParentNavTab.MISSIONS -> {
                        ParentMissionsScreen(
                            children = children,
                            missions = missions,
                            onSaveMission = { mission -> viewModel.saveMission(mission) },
                            onDeleteMission = { mission -> viewModel.deleteMission(mission) },
                            onApplyTemplate = { template, childId -> viewModel.applyTemplate(template, childId) }
                        )
                    }
                    ParentNavTab.PLANNING -> {
                        ParentPlanningScreen(
                            children = children,
                            missions = missions
                        )
                    }
                    ParentNavTab.REWARDS -> {
                        ParentRewardsScreen(
                            rewards = allRewards,
                            redemptions = allRedemptions,
                            onSaveReward = { r -> viewModel.saveReward(r) },
                            onDeleteReward = { r -> viewModel.deleteReward(r) },
                            onApproveRedemption = { id -> viewModel.approveRedemption(id) },
                            onDeliverRedemption = { id -> viewModel.deliverRedemption(id) },
                            onRejectRedemption = { id -> viewModel.rejectRedemption(id) }
                        )
                    }
                    ParentNavTab.CHILDREN -> {
                        ParentChildrenScreen(
                            children = children,
                            onSaveChild = { ch -> viewModel.saveChild(ch) },
                            onDeleteChild = { ch -> viewModel.deleteChild(ch) }
                        )
                    }
                    ParentNavTab.SETTINGS -> {
                        ParentSettingsScreen(
                            currentSettings = parentSettings,
                            hiveCode = hiveCode,
                            isCloudConnected = isCloudConnected,
                            onOpenFamilyLink = { showFamilyLinkDialog = true },
                            onSaveSettings = { s -> viewModel.saveParentSettings(s) },
                            onTestNotification = { viewModel.testSendParentNotification() },
                            onBackToKids = { isParentMode = false }
                        )
                    }
                }
            }
        }
    }

    // Celebration Dialog when child completes a mission
    celebrationEvent?.let { celebration ->
        CelebrationDialog(
            points = celebration.points,
            missionTitle = celebration.missionTitle,
            message = celebration.message,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    // Parent PIN Dialog
    if (showPinDialog) {
        ParentPinDialog(
            correctPin = parentSettings?.pinCode ?: "1234",
            onSuccess = {
                showPinDialog = false
                isParentMode = true
                parentTab = ParentNavTab.DASHBOARD
            },
            onDismiss = { showPinDialog = false }
        )
    }

    // Bee Customizer Dialog
    if (showBeeCustomizer && currentChild != null) {
        KidsBeeCustomizerDialog(
            currentHat = currentChild?.beeHat ?: "none",
            currentColor = currentChild?.beeColor ?: "honey_gold",
            onSave = { hat, color ->
                currentChild?.let { ch ->
                    viewModel.updateBeeCustomization(ch.id, hat, color)
                }
            },
            onDismiss = { showBeeCustomizer = false }
        )
    }

    // Family Link Multi-Phone Dialog
    FamilyLinkDialog(
        isOpen = showFamilyLinkDialog,
        onDismiss = { showFamilyLinkDialog = false },
        hiveCode = hiveCode,
        deviceRole = deviceRole,
        dedicatedChildId = dedicatedChildId,
        isCloudConnected = isCloudConnected,
        currentUser = currentUser,
        allChildren = children,
        familyName = parentSettings?.familyName ?: "Famille Abeille",
        parentPin = parentSettings?.pinCode ?: "1234",
        onSignInWithGoogle = { act -> viewModel.signInWithGoogle(act, onSuccess = {}, onError = {}) },
        onSignOut = { viewModel.signOut() },
        onCreateHive = { name, pin, cb -> viewModel.createHive(name, pin, cb) },
        onJoinHive = { code, role, childId, cb -> viewModel.joinHive(code, role, childId, cb) },
        onSetDeviceRole = { role, childId -> viewModel.setDeviceRole(role, childId) },
        onDisconnectHive = { viewModel.disconnectHive() },
        onTestNotification = { viewModel.testSendParentNotification() }
    )
}
