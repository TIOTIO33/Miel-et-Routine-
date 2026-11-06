package com.example.data.model

data class TemplateItem(
    val title: String,
    val description: String,
    val category: Category,
    val points: Int,
    val period: PeriodOfDay,
    val timeHint: String? = null,
    val iconKey: String = "star",
    val minAge: Int = 3,
    val maxAge: Int = 18,
    val requiresParentValidation: Boolean = false
)

data class RoutineTemplate(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val ageBracketLabel: String,
    val targetPeriod: PeriodOfDay,
    val defaultDays: String = "1,2,3,4,5,6,7",
    val items: List<TemplateItem>
) {
    companion object {
        val ALL_TEMPLATES: List<RoutineTemplate> = listOf(
            // 3-5 ans : Maternelle & Tout-petits
            RoutineTemplate(
                id = "toddler_morning",
                title = "Matin des Tout-Petits",
                description = "Pour les 3-5 ans : premières routines douces et guidées.",
                emoji = "🧸",
                ageBracketLabel = "👶 3-5 ans (Maternelle)",
                targetPeriod = PeriodOfDay.MATIN,
                defaultDays = "1,2,3,4,5",
                items = listOf(
                    TemplateItem("Ranger son doudou dans son lit", "Poser son doudou bien au chaud", Category.RANGEMENT, 2, PeriodOfDay.MATIN, "07:30", "bed", 3, 5, true),
                    TemplateItem("Enfiler ses pantoufles ou chaussures scratch", "Sans se tromper de pied !", Category.HABILLEMENT, 2, PeriodOfDay.MATIN, "07:45", "checkroom", 3, 6, true),
                    TemplateItem("Brosser les dents avec papa ou maman", "Frotter toutes les petites dents", Category.HYGIENE, 2, PeriodOfDay.MATIN, "08:00", "shower", 3, 6, true),
                    TemplateItem("Finir son petit déjeuner", "Manger son bol ou ses tartines", Category.REPAS, 2, PeriodOfDay.MATIN, "08:15", "restaurant", 3, 6, false)
                )
            ),
            RoutineTemplate(
                id = "toddler_evening",
                title = "Soir Doux des Tout-Petits",
                description = "Pour les 3-5 ans : ranger ses jouets et préparer un gros dodo.",
                emoji = "🌙",
                ageBracketLabel = "👶 3-5 ans (Maternelle)",
                targetPeriod = PeriodOfDay.SOIR,
                defaultDays = "1,2,3,4,5,6,7",
                items = listOf(
                    TemplateItem("Ranger 3 jouets dans le coffre", "Chaque jouet retrouve sa maison", Category.RANGEMENT, 2, PeriodOfDay.SOIR, "18:45", "inventory_2", 3, 5, true),
                    TemplateItem("Mettre son pyjama", "Glisser ses petits bras dans le haut", Category.HABILLEMENT, 2, PeriodOfDay.SOIR, "19:30", "checkroom", 3, 6, false),
                    TemplateItem("Histoire du soir & câlin", "Écouter sagement l'histoire", Category.SOMMEIL, 2, PeriodOfDay.SOIR, "20:00", "menu_book", 3, 6, false)
                )
            ),

            // 6-8 ans : Écoliers & Autonomie
            RoutineTemplate(
                id = "school_morning_6_8",
                title = "Matin de l'Écolier",
                description = "Pour les 6-8 ans : autonomie le matin sans retard pour l'école.",
                emoji = "🎒",
                ageBracketLabel = "🎒 6-8 ans (Écoliers)",
                targetPeriod = PeriodOfDay.MATIN,
                defaultDays = "1,2,4,5",
                items = listOf(
                    TemplateItem("Faire son lit", "Remettre sa couette et ses oreillers droits", Category.RANGEMENT, 2, PeriodOfDay.MATIN, "07:15", "bed", 6, 10, false),
                    TemplateItem("S'habiller tout seul", "Enfiler ses habits préparés", Category.HABILLEMENT, 2, PeriodOfDay.MATIN, "07:30", "checkroom", 5, 9, false),
                    TemplateItem("Brossage de dents 2 minutes", "Bien brosser haut et bas chrono", Category.HYGIENE, 2, PeriodOfDay.MATIN, "07:45", "shower", 5, 12, false),
                    TemplateItem("Vérifier son cartable", "Trousse, cahiers et cahier de texte", Category.ECOLE, 3, PeriodOfDay.MATIN, "08:10", "school", 6, 11, true)
                )
            ),
            RoutineTemplate(
                id = "homework_evening_6_8",
                title = "Devoirs & Soirée Réussie",
                description = "Pour les 6-8 ans : devoirs, bain et participation aux tâches.",
                emoji = "📚",
                ageBracketLabel = "🎒 6-8 ans (Écoliers)",
                targetPeriod = PeriodOfDay.SOIR,
                defaultDays = "1,2,4",
                items = listOf(
                    TemplateItem("Faire ses devoirs du soir", "Lecture, révisions et exercices au calme", Category.DEVOIRS, 5, PeriodOfDay.SOIR, "17:30", "menu_book", 6, 12, true),
                    TemplateItem("Débarrasser son assiette", "Apporter son verre et son assiette à la cuisine", Category.BIENVEILLANCE, 3, PeriodOfDay.SOIR, "19:45", "favorite", 5, 12, false),
                    TemplateItem("Préparer sa tenue du lendemain", "Gagner du temps pour le réveil", Category.AUTONOMIE, 3, PeriodOfDay.SOIR, "20:15", "checkroom", 6, 12, false)
                )
            ),

            // 8-12+ ans : Grands & Responsabilités
            RoutineTemplate(
                id = "older_kids_routine",
                title = "Missions des Grands",
                description = "Pour les 8 ans et plus : vraies responsabilités de grand et gestion autonome.",
                emoji = "🌟",
                ageBracketLabel = "🌟 8+ ans (Grands & Autonomie)",
                targetPeriod = PeriodOfDay.JOURNEE,
                defaultDays = "1,2,3,4,5,6,7",
                items = listOf(
                    TemplateItem("Vider le lave-vaisselle", "Ranger verres et couverts à leur place", Category.RESPONSABILITES, 4, PeriodOfDay.MIDI, null, "cleaning_services", 8, 18, true),
                    TemplateItem("Ranger sa chambre à fond", "Bureau propre, sol dégagé et affaires pliées", Category.RANGEMENT, 4, PeriodOfDay.JOURNEE, null, "home", 8, 18, false),
                    TemplateItem("Préparer son sac de sport & affaires", "Tenue complète, bouteille et baskets", Category.AUTONOMIE, 3, PeriodOfDay.JOURNEE, null, "inventory_2", 8, 18, false),
                    TemplateItem("Gérer ses devoirs en autonomie", "Devoirs finis et signés sans rappel", Category.DEVOIRS, 5, PeriodOfDay.SOIR, "18:00", "menu_book", 8, 18, true)
                )
            ),

            // Weekend & Famille
            RoutineTemplate(
                id = "weekend_routine",
                title = "Entraide & Ruche en Famille",
                description = "Activités sympas pour toute la ruche le weekend.",
                emoji = "🐝",
                ageBracketLabel = "🌈 Tous âges (3-18 ans)",
                targetPeriod = PeriodOfDay.JOURNEE,
                defaultDays = "6,7",
                items = listOf(
                    TemplateItem("Arroser une plante", "Donner un peu d'eau aux fleurs de la maison", Category.RESPONSABILITES, 3, PeriodOfDay.JOURNEE, null, "water_drop", 4, 18, false),
                    TemplateItem("Geste de gentillesse", "Faire un compliment ou aider un frère/sœur", Category.BIENVEILLANCE, 5, PeriodOfDay.JOURNEE, null, "favorite", 3, 18, false),
                    TemplateItem("Participer au rangement collectif", "Aider toute la famille pendant 15 minutes", Category.RANGEMENT, 4, PeriodOfDay.JOURNEE, null, "cleaning_services", 5, 18, false)
                )
            )
        )
    }
}
