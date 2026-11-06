package com.example.data.repository

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoutineRepository(private val database: AppDatabase) {
    private val childDao = database.childDao()
    private val missionDao = database.missionDao()
    private val rewardDao = database.rewardDao()
    private val badgeDao = database.badgeDao()
    private val parentSettingsDao = database.parentSettingsDao()

    val allChildren: Flow<List<ChildProfile>> = childDao.getAllChildren()
    val allMissions: Flow<List<Mission>> = missionDao.getAllMissions()
    val allRewards: Flow<List<Reward>> = rewardDao.getAllRewards()
    val activeRewards: Flow<List<Reward>> = rewardDao.getActiveRewards()
    val allRedemptions: Flow<List<RewardRedemption>> = rewardDao.getAllRedemptions()
    val pendingRedemptions: Flow<List<RewardRedemption>> = rewardDao.getPendingRedemptions()
    val pendingValidations: Flow<List<MissionCompletion>> = missionDao.getPendingValidations()
    val allCompletions: Flow<List<MissionCompletion>> = missionDao.getAllCompletions()
    val allBadges: Flow<List<Badge>> = badgeDao.getAllBadges()
    val parentSettings: Flow<ParentSettings?> = parentSettingsDao.getSettings()

    fun getMissionsForChild(childId: Long): Flow<List<Mission>> =
        missionDao.getMissionsForChild(childId)

    fun getCompletionsForDate(dateStr: String): Flow<List<MissionCompletion>> =
        missionDao.getCompletionsForDate(dateStr)

    fun getCompletionsForChildAndDate(childId: Long, dateStr: String): Flow<List<MissionCompletion>> =
        missionDao.getCompletionsForChildAndDate(childId, dateStr)

    fun getUnlockedBadgesForChild(childId: Long): Flow<List<ChildBadgeUnlock>> =
        badgeDao.getUnlockedBadgesForChild(childId)

    fun getChildById(childId: Long): Flow<ChildProfile?> =
        childDao.getChildById(childId)

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingChildren = childDao.getAllChildren().firstOrNull() ?: emptyList()
        if (existingChildren.isEmpty()) {
            // Insert Default Parent Settings
            parentSettingsDao.insertOrUpdate(
                ParentSettings(
                    id = 1,
                    pinCode = "1234",
                    familyName = "Famille Abeille",
                    morningReminderTime = "07:30",
                    eveningReminderTime = "19:30",
                    notificationsEnabled = true,
                    soundEffectsEnabled = true,
                    reducedAnimations = false,
                    defaultRequireParentValidation = false,
                    notifyOnValidation = true,
                    requireValidationUnderAge7 = true
                )
            )

            // Insert Children: Léo (4 ans - Maternelle, tâches adaptées) et Chloé (8 ans - CE2, tâches de grande)
            val leoId = childDao.insertChild(
                ChildProfile(
                    name = "Léo",
                    age = 4,
                    avatarKey = "bee_happy",
                    beeHat = "party",
                    beeColor = "honey_gold",
                    currentPoints = 14,
                    totalPointsEarned = 26,
                    completedMissionsCount = 8,
                    level = 2,
                    requiresParentValidation = true
                )
            )

            val chloeId = childDao.insertChild(
                ChildProfile(
                    name = "Chloé",
                    age = 8,
                    avatarKey = "bee_cool",
                    beeHat = "crown",
                    beeColor = "sky_blue",
                    currentPoints = 22,
                    totalPointsEarned = 45,
                    completedMissionsCount = 15,
                    level = 3,
                    requiresParentValidation = false
                )
            )

            // Insert Default Badges
            val initialBadges = listOf(
                Badge(key = "first_step", title = "Premier Pas", description = "A validé sa toute première mission !", iconKey = "emoji_events", targetCount = 1, badgeType = "FIRST_MISSION"),
                Badge(key = "ten_missions", title = "Petit Champion", description = "A terminé 10 missions comme un grand !", iconKey = "military_tech", targetCount = 10, badgeType = "MISSIONS_COUNT"),
                Badge(key = "super_autonomous", title = "Super Autonome", description = "20 missions accomplies avec brio !", iconKey = "stars", targetCount = 20, badgeType = "MISSIONS_COUNT"),
                Badge(key = "kind_bee", title = "Cœur de Miel", description = "A accompli 5 missions de bienveillance et d'entraide.", iconKey = "favorite", targetCount = 5, badgeType = "HELP_COUNT"),
                Badge(key = "week_hero", title = "Héros de la Ruche", description = "A complété toutes ses routines de la semaine.", iconKey = "workspace_premium", targetCount = 7, badgeType = "WEEK_CHAMPION"),
                Badge(key = "honey_collector", title = "Grand Butineur", description = "A cumulé 50 points de miel !", iconKey = "diamond", targetCount = 50, badgeType = "POINTS_GOAL")
            )
            badgeDao.insertBadges(initialBadges)

            badgeDao.unlockBadge(ChildBadgeUnlock(childId = leoId, badgeKey = "first_step"))
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = chloeId, badgeKey = "first_step"))
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = chloeId, badgeKey = "ten_missions"))

            // Missions adaptées selon l'âge
            val defaultMissions = listOf(
                // Spécifiques 3-5 ans (Tout-petits comme Léo 4 ans)
                Mission(childId = 0, title = "Ranger son doudou et oreiller", description = "Poser son doudou bien au chaud sur son lit", category = Category.RANGEMENT, points = 2, period = PeriodOfDay.MATIN, specificTime = "07:30", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "bed", minAge = 3, maxAge = 5),
                Mission(childId = 0, title = "Enfiler ses chaussures à scratch", description = "Sans se tromper de pied comme un grand", category = Category.HABILLEMENT, points = 2, period = PeriodOfDay.MATIN, specificTime = "07:45", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "checkroom", minAge = 3, maxAge = 6),
                Mission(childId = 0, title = "Ranger 3 jouets dans le bac", description = "Chaque doudou et petite voiture à sa place", category = Category.RANGEMENT, points = 2, period = PeriodOfDay.SOIR, specificTime = "18:45", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "inventory_2", minAge = 3, maxAge = 5),

                // Routines 5-8 ans & Écoliers (comme Chloé 8 ans)
                Mission(childId = 0, title = "Faire son lit", description = "Tirer sa couette et aligner son oreiller", category = Category.RANGEMENT, points = 2, period = PeriodOfDay.MATIN, specificTime = "07:30", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "bed", minAge = 5, maxAge = 18),
                Mission(childId = 0, title = "S'habiller tout seul", description = "Mettre ses vêtements propres", category = Category.HABILLEMENT, points = 2, period = PeriodOfDay.MATIN, specificTime = "07:45", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "checkroom", minAge = 4, maxAge = 12),
                Mission(childId = 0, title = "Préparer son cartable", description = "Cahiers, livres et trousse bien rangés", category = Category.ECOLE, points = 3, period = PeriodOfDay.MATIN, specificTime = "08:15", daysOfWeek = "1,2,4,5", iconKey = "school", minAge = 6, maxAge = 18),
                Mission(childId = 0, title = "Faire ses devoirs calmement", description = "Relire ses leçons et exercices du jour", category = Category.DEVOIRS, points = 5, period = PeriodOfDay.SOIR, specificTime = "17:30", daysOfWeek = "1,2,4", iconKey = "menu_book", minAge = 6, maxAge = 18, requiresParentValidation = true),
                Mission(childId = 0, title = "Débarrasser son assiette", description = "Apporter assiette et verre à la cuisine", category = Category.BIENVEILLANCE, points = 3, period = PeriodOfDay.SOIR, specificTime = "19:45", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "favorite", minAge = 5, maxAge = 18),

                // Routines 8 ans et plus (Grands & Responsabilités)
                Mission(childId = 0, title = "Vider le lave-vaisselle", description = "Ranger les verres et couverts à leur place", category = Category.RESPONSABILITES, points = 4, period = PeriodOfDay.MIDI, specificTime = null, daysOfWeek = "1,2,3,4,5,6,7", iconKey = "cleaning_services", minAge = 8, maxAge = 18, requiresParentValidation = true),
                Mission(childId = 0, title = "Préparer ses affaires de sport & tenue", description = "Baskets, gourde et tenue pour demain", category = Category.AUTONOMIE, points = 3, period = PeriodOfDay.SOIR, specificTime = "20:15", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "inventory_2", minAge = 8, maxAge = 18),
                Mission(childId = 0, title = "Ranger sa chambre à fond", description = "Bureau net, sol dégagé et lit au carré", category = Category.RANGEMENT, points = 4, period = PeriodOfDay.JOURNEE, specificTime = null, daysOfWeek = "6,7", iconKey = "home", minAge = 8, maxAge = 18, requiresParentValidation = true),

                // Transversales (tous âges)
                Mission(childId = 0, title = "Brosser les dents (matin)", description = "2 minutes pour des dents étincelantes", category = Category.HYGIENE, points = 2, period = PeriodOfDay.MATIN, specificTime = "08:00", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "shower", minAge = 3, maxAge = 18),
                Mission(childId = 0, title = "Se laver les mains avant de manger", description = "De l'eau tiède et du savon bien moussant", category = Category.HYGIENE, points = 2, period = PeriodOfDay.MIDI, specificTime = "12:00", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "wash", minAge = 3, maxAge = 18),
                Mission(childId = 0, title = "Mettre son pyjama", description = "Préparer le dodo en douceur", category = Category.HABILLEMENT, points = 2, period = PeriodOfDay.SOIR, specificTime = "20:00", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "checkroom", minAge = 3, maxAge = 12),
                Mission(childId = 0, title = "Brossage de dents du soir", description = "Des dents prêtes pour faire de beaux rêves", category = Category.HYGIENE, points = 2, period = PeriodOfDay.SOIR, specificTime = "20:15", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "shower", minAge = 3, maxAge = 18),
                Mission(childId = 0, title = "Câlin & histoire du soir", description = "Moment doux en famille pour bien dormir", category = Category.SOMMEIL, points = 2, period = PeriodOfDay.SOIR, specificTime = "20:30", daysOfWeek = "1,2,3,4,5,6,7", iconKey = "bedtime", minAge = 3, maxAge = 18),
                Mission(childId = 0, title = "Geste de gentillesse ou entraide", description = "Aider papa/maman ou son frère/sœur", category = Category.BIENVEILLANCE, points = 4, period = PeriodOfDay.JOURNEE, specificTime = null, daysOfWeek = "1,2,3,4,5,6,7", iconKey = "favorite", minAge = 3, maxAge = 18)
            )
            missionDao.insertMissions(defaultMissions)

            // Insert Default Rewards
            val defaultRewards = listOf(
                Reward(title = "Choisir le dessert du soir", description = "Crêpe, glace, yaourt ou fruit préféré !", costPoints = 8, iconKey = "cake"),
                Reward(title = "Choisir le film ou dessin animé", description = "C'est toi le programmateur de la soirée !", costPoints = 12, iconKey = "movie"),
                Reward(title = "Une histoire en plus le soir", description = "Une double dose d'aventures avec papa ou maman", costPoints = 6, iconKey = "menu_book"),
                Reward(title = "Sortie spéciale au parc", description = "Jeux, toboggans et balançoires !", costPoints = 15, iconKey = "park"),
                Reward(title = "Partie de jeu de société en famille", description = "Un jeu de ton choix tous ensemble", costPoints = 10, iconKey = "sports_esports"),
                Reward(title = "Atelier cuisine avec les parents", description = "Préparer des gâteaux ou une pizza maison", costPoints = 20, iconKey = "restaurant")
            )
            rewardDao.insertRewards(defaultRewards)
        }
    }

    // Complete / Uncomplete mission
    suspend fun completeMission(
        mission: Mission,
        child: ChildProfile,
        dateStr: String,
        forceAutoValidate: Boolean = false,
        settings: ParentSettings? = null
    ): Pair<Boolean, MissionCompletion> = withContext(Dispatchers.IO) {
        val requireUnder7 = (settings?.requireValidationUnderAge7 == true) && (child.age < 7)
        val defaultRequire = settings?.defaultRequireParentValidation == true
        val requiresValidation = if (forceAutoValidate) false else (
            mission.requiresParentValidation || child.requiresParentValidation || requireUnder7 || defaultRequire
        )
        val status = if (requiresValidation) "PENDING_PARENT" else "COMPLETED_AUTO"

        val completion = MissionCompletion(
            missionId = mission.id,
            childId = child.id,
            dateStr = dateStr,
            status = status,
            validatedTimestamp = if (!requiresValidation) System.currentTimeMillis() else null
        )
        val insertedId = missionDao.insertCompletion(completion)
        val saved = completion.copy(id = insertedId)

        if (!requiresValidation) {
            creditMissionSuccess(child.id, mission.points, mission.category)
        }
        return@withContext Pair(!requiresValidation, saved)
    }

    suspend fun uncompleteMission(missionId: Long, childId: Long, dateStr: String) = withContext(Dispatchers.IO) {
        missionDao.deleteCompletion(missionId, childId, dateStr)
    }

    suspend fun approveValidation(completion: MissionCompletion) = withContext(Dispatchers.IO) {
        val mission = missionDao.getMissionById(completion.missionId)
        val points = mission?.points ?: 2
        val category = mission?.category ?: Category.VIE_QUOTIDIENNE

        missionDao.updateCompletionStatus(completion.id, "APPROVED", System.currentTimeMillis())
        creditMissionSuccess(completion.childId, points, category)
    }

    suspend fun approveAllPendingValidations() = withContext(Dispatchers.IO) {
        val pending = missionDao.getPendingValidations().firstOrNull() ?: emptyList()
        for (completion in pending) {
            approveValidation(completion)
        }
    }

    suspend fun rejectValidation(completion: MissionCompletion) = withContext(Dispatchers.IO) {
        missionDao.updateCompletionStatus(completion.id, "REJECTED", System.currentTimeMillis())
    }

    suspend fun upsertRemoteCompletion(
        missionId: Long,
        childId: Long,
        dateStr: String,
        status: String,
        completedTimestamp: Long,
        validatedTimestamp: Long?
    ) = withContext(Dispatchers.IO) {
        val existing = missionDao.getCompletionsForDate(dateStr).firstOrNull()?.firstOrNull {
            it.missionId == missionId && it.childId == childId
        }
        if (existing == null) {
            missionDao.insertCompletion(
                MissionCompletion(
                    missionId = missionId,
                    childId = childId,
                    dateStr = dateStr,
                    status = status,
                    completedTimestamp = completedTimestamp,
                    validatedTimestamp = validatedTimestamp
                )
            )
            if (status == "APPROVED") {
                val mission = missionDao.getMissionById(missionId)
                val points = mission?.points ?: 2
                val category = mission?.category ?: Category.VIE_QUOTIDIENNE
                creditMissionSuccess(childId, points, category)
            }
        } else {
            if (existing.status != status) {
                missionDao.updateCompletionStatus(
                    existing.id,
                    status,
                    validatedTimestamp ?: System.currentTimeMillis()
                )
                if (status == "APPROVED" && existing.status != "APPROVED") {
                    val mission = missionDao.getMissionById(missionId)
                    val points = mission?.points ?: 2
                    val category = mission?.category ?: Category.VIE_QUOTIDIENNE
                    creditMissionSuccess(childId, points, category)
                }
            }
        }
    }

    private suspend fun creditMissionSuccess(childId: Long, points: Int, category: Category) {
        childDao.addPoints(childId, points)
        childDao.incrementCompletedCount(childId)

        val child = childDao.getChildByIdOnce(childId) ?: return
        val newCompleted = child.completedMissionsCount + 1
        val newTotalPoints = child.totalPointsEarned + points

        // Calculate Level
        val newLevel = when {
            newTotalPoints >= 100 -> 5
            newTotalPoints >= 60 -> 4
            newTotalPoints >= 35 -> 3
            newTotalPoints >= 15 -> 2
            else -> 1
        }
        if (newLevel != child.level) {
            childDao.updateLevel(childId, newLevel)
        }

        // Check badges
        if (newCompleted >= 1) {
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = childId, badgeKey = "first_step"))
        }
        if (newCompleted >= 10) {
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = childId, badgeKey = "ten_missions"))
        }
        if (newCompleted >= 20) {
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = childId, badgeKey = "super_autonomous"))
        }
        if (newTotalPoints >= 50) {
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = childId, badgeKey = "honey_collector"))
        }
        if (category == Category.BIENVEILLANCE) {
            badgeDao.unlockBadge(ChildBadgeUnlock(childId = childId, badgeKey = "kind_bee"))
        }
    }

    // Rewards & Redemptions
    suspend fun requestReward(reward: Reward, child: ChildProfile): Boolean = withContext(Dispatchers.IO) {
        if (child.currentPoints < reward.costPoints) return@withContext false

        // Deduct points from child's available points
        childDao.deductPoints(child.id, reward.costPoints)

        rewardDao.insertRedemption(
            RewardRedemption(
                rewardId = reward.id,
                rewardTitle = reward.title,
                childId = child.id,
                childName = child.name,
                costPoints = reward.costPoints,
                status = "PENDING"
            )
        )
        return@withContext true
    }

    suspend fun approveRewardRedemption(redemptionId: Long) = withContext(Dispatchers.IO) {
        rewardDao.updateRedemptionStatus(redemptionId, "APPROVED", System.currentTimeMillis())
    }

    suspend fun deliverRewardRedemption(redemptionId: Long) = withContext(Dispatchers.IO) {
        rewardDao.updateRedemptionStatus(redemptionId, "DELIVERED", System.currentTimeMillis())
    }

    suspend fun rejectRewardRedemption(redemptionId: Long) = withContext(Dispatchers.IO) {
        val redemption = rewardDao.getRedemptionById(redemptionId) ?: return@withContext
        // Refund points if rejected
        childDao.addPoints(redemption.childId, redemption.costPoints)
        rewardDao.updateRedemptionStatus(redemptionId, "REJECTED", System.currentTimeMillis())
    }

    // Children Management
    suspend fun insertChild(child: ChildProfile): Long = withContext(Dispatchers.IO) {
        childDao.insertChild(child)
    }

    suspend fun updateChild(child: ChildProfile) = withContext(Dispatchers.IO) {
        childDao.updateChild(child)
    }

    suspend fun deleteChild(child: ChildProfile) = withContext(Dispatchers.IO) {
        childDao.deleteChild(child)
    }

    suspend fun updateBeeCustomization(childId: Long, hat: String, color: String) = withContext(Dispatchers.IO) {
        childDao.updateBeeCustomization(childId, hat, color)
    }

    // Missions Management
    suspend fun insertMission(mission: Mission): Long = withContext(Dispatchers.IO) {
        missionDao.insertMission(mission)
    }

    suspend fun updateMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.updateMission(mission)
    }

    suspend fun deleteMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.deleteMission(mission)
    }

    suspend fun applyRoutineTemplate(template: RoutineTemplate, targetChildId: Long) = withContext(Dispatchers.IO) {
        val newMissions = template.items.map { item ->
            Mission(
                childId = targetChildId,
                title = item.title,
                description = item.description,
                category = item.category,
                points = item.points,
                period = item.period,
                specificTime = item.timeHint,
                daysOfWeek = template.defaultDays,
                iconKey = item.iconKey,
                minAge = item.minAge,
                maxAge = item.maxAge,
                requiresParentValidation = item.requiresParentValidation
            )
        }
        missionDao.insertMissions(newMissions)
    }

    // Rewards Management
    suspend fun insertReward(reward: Reward): Long = withContext(Dispatchers.IO) {
        rewardDao.insertReward(reward)
    }

    suspend fun updateReward(reward: Reward) = withContext(Dispatchers.IO) {
        rewardDao.updateReward(reward)
    }

    suspend fun deleteReward(reward: Reward) = withContext(Dispatchers.IO) {
        rewardDao.deleteReward(reward)
    }

    // Settings & PIN
    suspend fun updateParentSettings(settings: ParentSettings) = withContext(Dispatchers.IO) {
        parentSettingsDao.updateSettings(settings)
    }

    suspend fun verifyPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        val current = parentSettingsDao.getSettingsOnce()
        return@withContext (current?.pinCode ?: "1234") == pin
    }

    companion object {
        fun getTodayDateStr(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
