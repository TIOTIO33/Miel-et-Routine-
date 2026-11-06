package com.example.data.cloud

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.R
import com.example.data.model.Category
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import com.example.data.model.ParentSettings
import com.example.data.model.PeriodOfDay
import com.example.ui.notifications.ParentNotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class DeviceRole {
    SHARED, // Appareil partagé (mode local / familial sur un seul téléphone)
    PARENT, // Téléphone du parent (reçoit les alertes, valide)
    CHILD   // Téléphone ou tablette de l'enfant (exécute les tâches, reçoit miel)
}

class FirebaseSyncManager(private val context: Context) {
    // MANDATORY: Always initialize with R.string.firestore_database_id
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )
    private val auth = Firebase.auth
    private val prefs: SharedPreferences = context.getSharedPreferences("miel_sync_prefs", Context.MODE_PRIVATE)

    private val _hiveCode = MutableStateFlow(prefs.getString("hive_code", null))
    val hiveCode: StateFlow<String?> = _hiveCode.asStateFlow()

    private val _deviceRole = MutableStateFlow(
        try {
            DeviceRole.valueOf(prefs.getString("device_role", DeviceRole.SHARED.name) ?: DeviceRole.SHARED.name)
        } catch (e: Exception) {
            DeviceRole.SHARED
        }
    )
    val deviceRole: StateFlow<DeviceRole> = _deviceRole.asStateFlow()

    private val _dedicatedChildId = MutableStateFlow<Long?>(
        if (prefs.contains("dedicated_child_id")) prefs.getLong("dedicated_child_id", 0L) else null
    )
    val dedicatedChildId: StateFlow<Long?> = _dedicatedChildId.asStateFlow()

    private val _isCloudConnected = MutableStateFlow(false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private var completionsListener: ListenerRegistration? = null
    private var childrenListener: ListenerRegistration? = null

    init {
        val savedCode = _hiveCode.value
        if (!savedCode.isNullOrBlank() && auth.currentUser != null) {
            _isCloudConnected.value = true
        }
    }

    fun setDeviceRole(role: DeviceRole, childId: Long? = null) {
        _deviceRole.value = role
        _dedicatedChildId.value = childId
        prefs.edit()
            .putString("device_role", role.name)
            .apply {
                if (childId != null) putLong("dedicated_child_id", childId) else remove("dedicated_child_id")
            }
            .apply()
    }

    suspend fun createFamilyHive(
        familyName: String,
        parentPin: String,
        localChildren: List<ChildProfile>,
        localMissions: List<Mission>
    ): String = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: "user_${System.currentTimeMillis()}"
        val code = "RUCHE-${(100..999).random()}"

        val hiveData = hashMapOf(
            "hiveCode" to code,
            "familyName" to familyName,
            "ownerUid" to uid,
            "parentPin" to parentPin,
            "memberUids" to listOf(uid),
            "notifyOnValidation" to true,
            "requireValidationUnderAge7" to true,
            "defaultRequireParentValidation" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val hiveDoc = firestore.collection("hives").document(code)
        hiveDoc.set(hiveData).await()

        // Upload initial children to cloud hive
        for (child in localChildren) {
            hiveDoc.collection("children").document(child.id.toString()).set(
                hashMapOf(
                    "childId" to child.id.toString(),
                    "name" to child.name,
                    "age" to child.age,
                    "avatarKey" to child.avatarKey,
                    "beeHat" to child.beeHat,
                    "beeColor" to child.beeColor,
                    "currentPoints" to child.currentPoints,
                    "totalPointsEarned" to child.totalPointsEarned,
                    "completedMissionsCount" to child.completedMissionsCount,
                    "level" to child.level,
                    "requiresParentValidation" to child.requiresParentValidation,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

        // Upload initial missions to cloud hive
        for (mission in localMissions) {
            hiveDoc.collection("missions").document(mission.id.toString()).set(
                hashMapOf(
                    "missionId" to mission.id.toString(),
                    "childId" to mission.childId.toString(),
                    "title" to mission.title,
                    "description" to mission.description,
                    "category" to mission.category.name,
                    "points" to mission.points,
                    "period" to mission.period.name,
                    "specificTime" to (mission.specificTime ?: ""),
                    "daysOfWeek" to mission.daysOfWeek,
                    "iconKey" to mission.iconKey,
                    "requiresParentValidation" to mission.requiresParentValidation,
                    "minAge" to mission.minAge,
                    "maxAge" to mission.maxAge,
                    "isArchived" to false,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

        _hiveCode.value = code
        _isCloudConnected.value = true
        prefs.edit().putString("hive_code", code).apply()
        return@withContext code
    }

    suspend fun joinFamilyHive(
        code: String,
        role: DeviceRole = DeviceRole.CHILD,
        childId: Long? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cleanCode = code.trim().uppercase()
            val hiveDoc = firestore.collection("hives").document(cleanCode)
            val snapshot = hiveDoc.get().await()

            if (!snapshot.exists()) {
                return@withContext false
            }

            // Register current user as member
            auth.currentUser?.uid?.let { uid ->
                hiveDoc.update("memberUids", FieldValue.arrayUnion(uid))
            }

            _hiveCode.value = cleanCode
            _isCloudConnected.value = true
            setDeviceRole(role, childId)
            prefs.edit().putString("hive_code", cleanCode).apply()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erreur lors de la connexion à la ruche", e)
            return@withContext false
        }
    }

    fun startRealtimeSync(
        scope: CoroutineScope,
        onCompletionUpdated: (
            missionId: Long,
            childId: Long,
            dateStr: String,
            status: String,
            completedTimestamp: Long,
            validatedTimestamp: Long?
        ) -> Unit,
        onNewMissionPendingValidation: (childName: String, childAge: Int, missionTitle: String, points: Int) -> Unit,
        onMissionApprovedForChild: (childId: Long, points: Int, missionTitle: String) -> Unit
    ) {
        val code = _hiveCode.value ?: return
        completionsListener?.remove()

        val completionsRef = firestore.collection("hives").document(code).collection("completions")
        completionsListener = completionsRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            for (change in snapshot.documentChanges) {
                val data = change.document.data
                val status = data["status"] as? String ?: continue
                val childName = data["childName"] as? String ?: "Enfant"
                val childAge = (data["childAge"] as? Number)?.toInt() ?: 5
                val missionTitle = data["missionTitle"] as? String ?: "Mission"
                val points = (data["points"] as? Number)?.toInt() ?: 2
                val missionId = (data["missionId"] as? String)?.toLongOrNull() ?: 0L
                val childId = (data["childId"] as? String)?.toLongOrNull() ?: 0L
                val dateStr = data["dateStr"] as? String ?: ""
                val completedTimestamp = (data["completedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                val validatedTimestamp = (data["validatedTimestamp"] as? Number)?.toLong()

                when (change.type) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        // Keep local Room database in sync
                        if (missionId > 0 && childId > 0 && dateStr.isNotBlank()) {
                            onCompletionUpdated(
                                missionId,
                                childId,
                                dateStr,
                                status,
                                completedTimestamp,
                                validatedTimestamp
                            )
                        }

                        if (status == "PENDING_PARENT") {
                            // If on Parent phone: alert parent immediately!
                            if (_deviceRole.value == DeviceRole.PARENT || _deviceRole.value == DeviceRole.SHARED) {
                                ParentNotificationHelper.sendMissionValidationNotification(
                                    context = context,
                                    childName = childName,
                                    childAge = childAge,
                                    missionTitle = missionTitle,
                                    points = points,
                                    requiresApproval = true
                                )
                                onNewMissionPendingValidation(childName, childAge, missionTitle, points)
                            }
                        } else if (status == "APPROVED") {
                            // If on Child phone: trigger celebration and honey!
                            if (_deviceRole.value == DeviceRole.CHILD && _dedicatedChildId.value == childId) {
                                onMissionApprovedForChild(childId, points, missionTitle)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    suspend fun postCompletionToCloud(
        completion: MissionCompletion,
        childName: String,
        childAge: Int,
        missionTitle: String,
        points: Int
    ) = withContext(Dispatchers.IO) {
        val code = _hiveCode.value ?: return@withContext
        val compId = "${completion.missionId}_${completion.childId}_${completion.dateStr}"

        val data = hashMapOf(
            "completionId" to compId,
            "missionId" to completion.missionId.toString(),
            "childId" to completion.childId.toString(),
            "childName" to childName,
            "childAge" to childAge,
            "missionTitle" to missionTitle,
            "points" to points,
            "dateStr" to completion.dateStr,
            "status" to completion.status,
            "completedTimestamp" to completion.completedTimestamp,
            "validatedTimestamp" to completion.validatedTimestamp,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("hives").document(code)
            .collection("completions").document(compId)
            .set(data).await()
    }

    suspend fun approveCompletionInCloud(completion: MissionCompletion, points: Int) = withContext(Dispatchers.IO) {
        val code = _hiveCode.value ?: return@withContext
        val compId = "${completion.missionId}_${completion.childId}_${completion.dateStr}"

        val hiveDoc = firestore.collection("hives").document(code)
        hiveDoc.collection("completions").document(compId).update(
            "status", "APPROVED",
            "validatedTimestamp", System.currentTimeMillis(),
            "updatedAt", FieldValue.serverTimestamp()
        ).await()

        // Increment points in cloud child profile
        hiveDoc.collection("children").document(completion.childId.toString()).update(
            "currentPoints", FieldValue.increment(points.toLong()),
            "totalPointsEarned", FieldValue.increment(points.toLong()),
            "completedMissionsCount", FieldValue.increment(1),
            "updatedAt", FieldValue.serverTimestamp()
        ).await()
    }

    suspend fun rejectCompletionInCloud(completion: MissionCompletion) = withContext(Dispatchers.IO) {
        val code = _hiveCode.value ?: return@withContext
        val compId = "${completion.missionId}_${completion.childId}_${completion.dateStr}"

        firestore.collection("hives").document(code)
            .collection("completions").document(compId).update(
                "status", "REJECTED",
                "updatedAt", FieldValue.serverTimestamp()
            ).await()
    }

    fun disconnect() {
        completionsListener?.remove()
        childrenListener?.remove()
        _hiveCode.value = null
        _isCloudConnected.value = false
        prefs.edit().remove("hive_code").apply()
    }
}
