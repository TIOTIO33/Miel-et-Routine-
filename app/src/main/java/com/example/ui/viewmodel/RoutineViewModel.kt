package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.DeviceRole
import com.example.data.cloud.FirebaseAuthManager
import com.example.data.cloud.FirebaseSyncManager
import com.example.data.local.AppDatabase
import com.example.data.model.Badge
import com.example.data.model.Category
import com.example.data.model.ChildBadgeUnlock
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import com.example.data.model.ParentSettings
import com.example.data.model.PeriodOfDay
import com.example.data.model.Reward
import com.example.data.model.RewardRedemption
import com.example.data.model.RoutineTemplate
import com.example.data.repository.RoutineRepository
import com.example.ui.notifications.ParentNotificationHelper
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RoutineViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = RoutineRepository(database)

    val authManager = FirebaseAuthManager(application)
    val syncManager = FirebaseSyncManager(application)

    val hiveCode: StateFlow<String?> = syncManager.hiveCode
    val deviceRole: StateFlow<DeviceRole> = syncManager.deviceRole
    val dedicatedChildId: StateFlow<Long?> = syncManager.dedicatedChildId
    val isCloudConnected: StateFlow<Boolean> = syncManager.isCloudConnected
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser

    private val _selectedChildId = MutableStateFlow<Long?>(null)
    val selectedChildId: StateFlow<Long?> = _selectedChildId.asStateFlow()

    private val _selectedDateStr = MutableStateFlow(RoutineRepository.getTodayDateStr())
    val selectedDateStr: StateFlow<String> = _selectedDateStr.asStateFlow()

    private val _celebrationEvent = MutableStateFlow<CelebrationData?>(null)
    val celebrationEvent: StateFlow<CelebrationData?> = _celebrationEvent.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    // Data streams
    val allChildren: StateFlow<List<ChildProfile>> = repository.allChildren
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMissions: StateFlow<List<Mission>> = repository.allMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRewards: StateFlow<List<Reward>> = repository.activeRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRewards: StateFlow<List<Reward>> = repository.allRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRedemptions: StateFlow<List<RewardRedemption>> = repository.allRedemptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingRedemptions: StateFlow<List<RewardRedemption>> = repository.pendingRedemptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingValidations: StateFlow<List<MissionCompletion>> = repository.pendingValidations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompletions: StateFlow<List<MissionCompletion>> = repository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBadges: StateFlow<List<Badge>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentSettings: StateFlow<ParentSettings?> = repository.parentSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current selected child
    val currentChild: StateFlow<ChildProfile?> = combine(allChildren, _selectedChildId) { list, id ->
        val dedicatedId = dedicatedChildId.value
        val targetId = if (deviceRole.value == DeviceRole.CHILD && dedicatedId != null) dedicatedId else id

        if (targetId == null && list.isNotEmpty()) {
            _selectedChildId.value = list.first().id
            list.first()
        } else {
            list.firstOrNull { it.id == targetId } ?: list.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Completions for selected date
    val completionsForSelectedDate: StateFlow<List<MissionCompletion>> = _selectedDateStr
        .flatMapLatest { date -> repository.getCompletionsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Badges unlocked for current child
    val currentChildBadges: StateFlow<List<ChildBadgeUnlock>> = currentChild
        .flatMapLatest { child ->
            if (child != null) repository.getUnlockedBadgesForChild(child.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        ParentNotificationHelper.initChannel(application)
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            setupCloudSyncListener()
        }
    }

    private fun setupCloudSyncListener() {
        syncManager.startRealtimeSync(
            scope = viewModelScope,
            onCompletionUpdated = { missionId, childId, dateStr, status, completedTimestamp, validatedTimestamp ->
                viewModelScope.launch {
                    repository.upsertRemoteCompletion(
                        missionId = missionId,
                        childId = childId,
                        dateStr = dateStr,
                        status = status,
                        completedTimestamp = completedTimestamp,
                        validatedTimestamp = validatedTimestamp
                    )
                }
            },
            onNewMissionPendingValidation = { childName, childAge, missionTitle, points ->
                _snackMessage.value = "Nouvelle mission de $childName ($childAge ans) à valider !"
            },
            onMissionApprovedForChild = { childId, points, missionTitle ->
                val child = allChildren.value.firstOrNull { it.id == childId }
                val childName = child?.name ?: "Enfant"
                _celebrationEvent.value = CelebrationData(
                    points = points,
                    missionTitle = missionTitle,
                    message = "Super $childName ! Mission validée par papa/maman : +$points miel 🍯 !"
                )
            }
        )
    }

    fun selectChild(childId: Long) {
        _selectedChildId.value = childId
    }

    fun selectDate(dateStr: String) {
        _selectedDateStr.value = dateStr
    }

    fun shiftDate(days: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val date = sdf.parse(_selectedDateStr.value) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DAY_OF_YEAR, days)
            _selectedDateStr.value = sdf.format(cal.time)
        } catch (e: Exception) {
            _selectedDateStr.value = RoutineRepository.getTodayDateStr()
        }
    }

    fun toggleMission(mission: Mission, child: ChildProfile, dateStr: String) {
        viewModelScope.launch {
            val completions = completionsForSelectedDate.value
            val existing = completions.firstOrNull { it.missionId == mission.id && it.childId == child.id && it.dateStr == dateStr }

            if (existing != null) {
                // If it was already completed, toggle off
                repository.uncompleteMission(mission.id, child.id, dateStr)
                _snackMessage.value = "Mission remise à faire"
            } else {
                val settings = parentSettings.value
                val (autoValidated, completion) = repository.completeMission(
                    mission = mission,
                    child = child,
                    dateStr = dateStr,
                    settings = settings
                )

                // Sync to Firestore for real-time multi-device parent phone alert & dashboard sync
                syncManager.postCompletionToCloud(
                    completion = completion,
                    childName = child.name,
                    childAge = child.age,
                    missionTitle = mission.title,
                    points = mission.points
                )

                // Dispatch notification to parent phone if enabled
                if (settings?.notifyOnValidation != false) {
                    ParentNotificationHelper.sendMissionValidationNotification(
                        context = getApplication(),
                        childName = child.name,
                        childAge = child.age,
                        missionTitle = mission.title,
                        points = mission.points,
                        requiresApproval = !autoValidated
                    )
                }

                if (autoValidated) {
                    _celebrationEvent.value = CelebrationData(
                        points = mission.points,
                        missionTitle = mission.title,
                        message = "Bravo ${child.name} ! +${mission.points} gouttes de miel 🍯 !"
                    )
                } else {
                    _snackMessage.value = "Notification envoyée au parent pour validation ! ✨"
                }
            }
        }
    }

    fun requestReward(reward: Reward, child: ChildProfile) {
        viewModelScope.launch {
            val success = repository.requestReward(reward, child)
            if (success) {
                // Dispatch notification to parent phone
                if (parentSettings.value?.notifyOnValidation != false) {
                    ParentNotificationHelper.sendRewardRequestNotification(
                        context = getApplication(),
                        childName = child.name,
                        childAge = child.age,
                        rewardTitle = reward.title,
                        costPoints = reward.costPoints
                    )
                }
                _snackMessage.value = "Demande envoyée sur le téléphone des parents ! 🎁"
            } else {
                _snackMessage.value = "Oups ! Il te manque des points de miel."
            }
        }
    }

    fun approveValidation(completion: MissionCompletion) {
        viewModelScope.launch {
            repository.approveValidation(completion)
            val mission = allMissions.value.firstOrNull { it.id == completion.missionId }
            val points = mission?.points ?: 2
            syncManager.approveCompletionInCloud(completion, points)
            _snackMessage.value = "Mission validée ! Points accordés 🍯"
        }
    }

    fun approveAllPendingValidations() {
        viewModelScope.launch {
            val pending = pendingValidations.value
            for (comp in pending) {
                repository.approveValidation(comp)
                val mission = allMissions.value.firstOrNull { it.id == comp.missionId }
                val points = mission?.points ?: 2
                syncManager.approveCompletionInCloud(comp, points)
            }
            _snackMessage.value = "Toutes les missions en attente ont été validées ! 🎉"
        }
    }

    fun rejectValidation(completion: MissionCompletion) {
        viewModelScope.launch {
            repository.rejectValidation(completion)
            syncManager.rejectCompletionInCloud(completion)
            _snackMessage.value = "Validation refusée"
        }
    }

    fun createHive(familyName: String, parentPin: String, onDone: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val code = syncManager.createFamilyHive(
                    familyName = familyName,
                    parentPin = parentPin,
                    localChildren = allChildren.value,
                    localMissions = allMissions.value
                )
                setupCloudSyncListener()
                _snackMessage.value = "Ruche familiale créée : Code $code 🐝"
                onDone(code)
            } catch (e: Exception) {
                _snackMessage.value = "Erreur création ruche : ${e.localizedMessage}"
            }
        }
    }

    fun joinHive(code: String, role: DeviceRole, childId: Long?, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = syncManager.joinFamilyHive(code, role, childId)
            if (success) {
                setupCloudSyncListener()
                if (role == DeviceRole.CHILD && childId != null) {
                    _selectedChildId.value = childId
                }
                _snackMessage.value = "Connecté à la ruche $code ! ✨"
                onDone(true)
            } else {
                _snackMessage.value = "Code de ruche invalide ou introuvable"
                onDone(false)
            }
        }
    }

    fun setDeviceRole(role: DeviceRole, childId: Long? = null) {
        syncManager.setDeviceRole(role, childId)
        if (role == DeviceRole.CHILD && childId != null) {
            _selectedChildId.value = childId
        }
        _snackMessage.value = "Appareil configuré en mode ${when(role) {
            DeviceRole.PARENT -> "Téléphone Parent 📱"
            DeviceRole.CHILD -> "Téléphone Enfant 🧒"
            DeviceRole.SHARED -> "Appareil Partagé 🏠"
        }}"
    }

    fun disconnectHive() {
        syncManager.disconnect()
        _snackMessage.value = "Déconnecté de la ruche cloud"
    }

    fun signInWithGoogle(activity: Activity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = { user ->
                _snackMessage.value = "Connecté avec ${user.displayName ?: user.email} !"
                onSuccess()
            },
            onError = { err ->
                _snackMessage.value = "Connexion : $err"
                onError(err)
            }
        )
    }

    fun signOut(onComplete: () -> Unit = {}) {
        authManager.signOut(viewModelScope) {
            syncManager.disconnect()
            _snackMessage.value = "Déconnecté du compte Google"
            onComplete()
        }
    }

    fun approveRedemption(redemptionId: Long) {
        viewModelScope.launch {
            repository.approveRewardRedemption(redemptionId)
            _snackMessage.value = "Récompense approuvée ! 🎉"
        }
    }

    fun deliverRedemption(redemptionId: Long) {
        viewModelScope.launch {
            repository.deliverRewardRedemption(redemptionId)
            _snackMessage.value = "Récompense remise à l'enfant !"
        }
    }

    fun rejectRedemption(redemptionId: Long) {
        viewModelScope.launch {
            repository.rejectRewardRedemption(redemptionId)
            _snackMessage.value = "Demande refusée. Points remboursés."
        }
    }

    fun updateBeeCustomization(childId: Long, hat: String, color: String) {
        viewModelScope.launch {
            repository.updateBeeCustomization(childId, hat, color)
            _snackMessage.value = "Abeille relookée avec succès ! 🐝"
        }
    }

    fun applyTemplate(template: RoutineTemplate, targetChildId: Long) {
        viewModelScope.launch {
            repository.applyRoutineTemplate(template, targetChildId)
            _snackMessage.value = "Modèle '${template.title}' appliqué !"
        }
    }

    fun saveMission(mission: Mission) {
        viewModelScope.launch {
            if (mission.id == 0L) {
                repository.insertMission(mission)
                _snackMessage.value = "Mission créée (${mission.ageRangeLabel}) !"
            } else {
                repository.updateMission(mission)
                _snackMessage.value = "Mission modifiée (${mission.ageRangeLabel}) !"
            }
        }
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch {
            repository.deleteMission(mission)
            _snackMessage.value = "Mission supprimée"
        }
    }

    fun saveChild(child: ChildProfile) {
        viewModelScope.launch {
            if (child.id == 0L) {
                repository.insertChild(child)
                _snackMessage.value = "Profil de ${child.name} (${child.age} ans) ajouté !"
            } else {
                repository.updateChild(child)
                _snackMessage.value = "Profil de ${child.name} (${child.age} ans) mis à jour !"
            }
        }
    }

    fun deleteChild(child: ChildProfile) {
        viewModelScope.launch {
            repository.deleteChild(child)
            _snackMessage.value = "Profil supprimé"
        }
    }

    fun saveReward(reward: Reward) {
        viewModelScope.launch {
            if (reward.id == 0L) {
                repository.insertReward(reward)
                _snackMessage.value = "Récompense ajoutée !"
            } else {
                repository.updateReward(reward)
                _snackMessage.value = "Récompense modifiée !"
            }
        }
    }

    fun deleteReward(reward: Reward) {
        viewModelScope.launch {
            repository.deleteReward(reward)
            _snackMessage.value = "Récompense supprimée"
        }
    }

    fun saveParentSettings(settings: ParentSettings) {
        viewModelScope.launch {
            repository.updateParentSettings(settings)
            _snackMessage.value = "Paramètres de contrôle parental enregistrés !"
        }
    }

    fun testSendParentNotification() {
        ParentNotificationHelper.sendTestNotification(getApplication())
        _snackMessage.value = "Notification de test envoyée sur le téléphone ! 🔔"
    }

    fun dismissCelebration() {
        _celebrationEvent.value = null
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    suspend fun verifyPin(pin: String): Boolean {
        return repository.verifyPin(pin)
    }
}

data class CelebrationData(
    val points: Int,
    val missionTitle: String,
    val message: String
)
