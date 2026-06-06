package com.example.data.models

data class GamificationUnlockable(
    val id: String,
    val name: String,
    val type: String, // "Badge" or "Skin"
    val aesthetic: String, // "Cyberpunk", "Mecha", "Sci-Fi", "Anime"
    val description: String,
    val iconEmoji: String,
    val unlockConditionDesc: String,
    val isXpMilestone: Boolean,
    val xpRequired: Int = 0,
    val categoryRequired: String? = null,
    val completionCountReq: Int = 0,
    val streakRequired: Int = 0
)

object MilestoneSystem {
    // --- Requirement 3: JSON Configuration of at least 10 unlockables ---
    val GAMIFICATION_CONFIG_JSON = """
    [
      {
        "id": "neo_novice",
        "name": "Neo-Novice Protagonist",
        "type": "Skin",
        "aesthetic": "Anime Cyberpunk",
        "description": "A rookie runner with a glowing holographic visual jacket.",
        "iconEmoji": "🧑‍🎤",
        "unlockConditionDesc": "Reach Level 2 (200 XP)",
        "isXpMilestone": true,
        "xpRequired": 200,
        "categoryRequired": null,
        "completionCountReq": 0,
        "streakRequired": 0
      },
      {
        "id": "cyber_samurai",
        "name": "Cyber-Samurai Focus",
        "type": "Badge",
        "aesthetic": "Neon Edged",
        "description": "You have sharpened your daily mind. Razor-sharp focus unlocked.",
        "iconEmoji": "⚔️",
        "unlockConditionDesc": "Complete 5 Personal tasks",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": "Personal",
        "completionCountReq": 5,
        "streakRequired": 0
      },
      {
        "id": "quantum_architect",
        "name": "Quantum Architect",
        "type": "Skin",
        "aesthetic": "Sleek Visor",
        "description": "Code and systems analyst. Sleek, glowing cosmic HUD visor skin.",
        "iconEmoji": "🥽",
        "unlockConditionDesc": "Reach level 3 (400 XP)",
        "isXpMilestone": true,
        "xpRequired": 400,
        "categoryRequired": null,
        "completionCountReq": 0,
        "streakRequired": 0
      },
      {
        "id": "mecha_overlord",
        "name": "Mecha Overlord",
        "type": "Skin",
        "aesthetic": "Heavy Gundam",
        "description": "Heavy industrial steel plated armor suit for extreme productivity output.",
        "iconEmoji": "🤖",
        "unlockConditionDesc": "Complete 5 Work tasks",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": "Work",
        "completionCountReq": 5,
        "streakRequired": 0
      },
      {
        "id": "zen_synth_master",
        "name": "Zen Synth Master",
        "type": "Badge",
        "aesthetic": "Holo Lotus",
        "description": "A glowing holographic floating lotus accessory orbiting your player.",
        "iconEmoji": "🌸",
        "unlockConditionDesc": "Complete 10 Personal tasks",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": "Personal",
        "completionCountReq": 10,
        "streakRequired": 0
      },
      {
        "id": "glitch_runner",
        "name": "Glitch Runner",
        "type": "Skin",
        "aesthetic": "Chrono Hack",
        "description": "Defy the database constraints! Cyberpunk street clothes with hoverboard.",
        "iconEmoji": "🛹",
        "unlockConditionDesc": "Accomplish a 3-day active habit streak",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": null,
        "completionCountReq": 0,
        "streakRequired": 3
      },
      {
        "id": "chronos_controller",
        "name": "Chronos Controller",
        "type": "Skin",
        "aesthetic": "Steampunk Cyber",
        "description": "Time bends to your daily execution. Chronos brass wristwatch accessory.",
        "iconEmoji": "⌚",
        "unlockConditionDesc": "Accomplish a 5-day active habit streak",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": null,
        "completionCountReq": 0,
        "streakRequired": 5
      },
      {
        "id": "ludus_apex",
        "name": "Others Explorer Champion",
        "type": "Badge",
        "aesthetic": "Arcade Gold",
        "description": "Engage productively beyond work. Golden retro arcade retro console aura.",
        "iconEmoji": "🧩",
        "unlockConditionDesc": "Complete 5 Other tasks",
        "isXpMilestone": false,
        "xpRequired": 0,
        "categoryRequired": "Others",
        "completionCountReq": 5,
        "streakRequired": 0
      },
      {
        "id": "hyperion_voyager",
        "name": "Hyperion Space Voyager",
        "type": "Skin",
        "aesthetic": "Sci-Fi Armored",
        "description": "High-altitude cosmic explorer space armor with neon thrusters.",
        "iconEmoji": "🚀",
        "unlockConditionDesc": "Reach Level 5 (800 XP)",
        "isXpMilestone": true,
        "xpRequired": 800,
        "categoryRequired": null,
        "completionCountReq": 0,
        "streakRequired": 0
      },
      {
        "id": "grandmaster_ai",
        "name": "Grandmaster AI Catalyst",
        "type": "Skin",
        "aesthetic": "God-Tier Celestial",
        "description": "The peak of human-machine integration. Ultimate shining cosmic aura skin.",
        "iconEmoji": "🌌",
        "unlockConditionDesc": "Completes 10 Work tasks & Level 5+",
        "isXpMilestone": false,
        "xpRequired": 800,
        "categoryRequired": "Work",
        "completionCountReq": 10,
        "streakRequired": 0
      }
    ]
    """.trimIndent()

    val unlockablesList = listOf(
        GamificationUnlockable(
            id = "neo_novice",
            name = "Neo-Novice Protagonist",
            type = "Skin",
            aesthetic = "Anime Cyberpunk",
            description = "A rookie runner equipped with a glowing holographic visual jacket.",
            iconEmoji = "🧑‍🎤",
            unlockConditionDesc = "Reach Level 2 (200 XP)",
            isXpMilestone = true,
            xpRequired = 200
        ),
        GamificationUnlockable(
            id = "cyber_samurai",
            name = "Cyber-Samurai Focus",
            type = "Badge",
            aesthetic = "Neon Edged",
            description = "You have sharpened your daily mind. Razor-sharp focus unlocked.",
            iconEmoji = "⚔️",
            unlockConditionDesc = "Complete 5 Personal tasks",
            isXpMilestone = false,
            categoryRequired = "Personal",
            completionCountReq = 5
        ),
        GamificationUnlockable(
            id = "quantum_architect",
            name = "Quantum Architect",
            type = "Skin",
            aesthetic = "Sleek Visor",
            description = "Code and systems analyst. Sleek, glowing cosmic HUD visor skin.",
            iconEmoji = "🥽",
            unlockConditionDesc = "Reach level 3 (400 XP)",
            isXpMilestone = true,
            xpRequired = 400
        ),
        GamificationUnlockable(
            id = "mecha_overlord",
            name = "Mecha Overlord",
            type = "Skin",
            aesthetic = "Heavy Gundam",
            description = "Heavy industrial steel plated armor suit for extreme productivity output.",
            iconEmoji = "🤖",
            unlockConditionDesc = "Complete 5 Work tasks",
            isXpMilestone = false,
            categoryRequired = "Work",
            completionCountReq = 5
        ),
        GamificationUnlockable(
            id = "zen_synth_master",
            name = "Zen Synth Master",
            type = "Badge",
            aesthetic = "Holo Lotus",
            description = "A glowing holographic floating lotus accessory orbiting your player.",
            iconEmoji = "🌸",
            unlockConditionDesc = "Complete 10 Personal tasks",
            isXpMilestone = false,
            categoryRequired = "Personal",
            completionCountReq = 10
        ),
        GamificationUnlockable(
            id = "glitch_runner",
            name = "Glitch Runner",
            type = "Skin",
            aesthetic = "Chrono Hack",
            description = "Defy the database constraints! Cyberpunk street clothes with hoverboard.",
            iconEmoji = "🛹",
            unlockConditionDesc = "Accomplish a 3-day active habit streak",
            isXpMilestone = false,
            streakRequired = 3
        ),
        GamificationUnlockable(
            id = "chronos_controller",
            name = "Chronos Controller",
            type = "Skin",
            aesthetic = "Steampunk Cyber",
            description = "Time bends to your daily execution. Chronos brass wristwatch accessory.",
            iconEmoji = "⌚",
            unlockConditionDesc = "Accomplish a 5-day active habit streak",
            isXpMilestone = false,
            streakRequired = 5
        ),
        GamificationUnlockable(
            id = "ludus_apex",
            name = "Others Explorer Champion",
            type = "Badge",
            aesthetic = "Arcade Gold",
            description = "Engage productively beyond work. Golden retro arcade retro console aura.",
            iconEmoji = "🧩",
            unlockConditionDesc = "Complete 5 Other tasks",
            isXpMilestone = false,
            categoryRequired = "Others",
            completionCountReq = 5
        ),
        GamificationUnlockable(
            id = "hyperion_voyager",
            name = "Hyperion Space Voyager",
            type = "Skin",
            aesthetic = "Sci-Fi Armored",
            description = "High-altitude cosmic explorer space armor with neon thrusters.",
            iconEmoji = "🚀",
            unlockConditionDesc = "Reach Level 5 (800 XP)",
            isXpMilestone = true,
            xpRequired = 800
        ),
        GamificationUnlockable(
            id = "grandmaster_ai",
            name = "Grandmaster AI Catalyst",
            type = "Skin",
            aesthetic = "God-Tier Celestial",
            description = "The peak of human-machine integration. Ultimate shining cosmic aura skin.",
            iconEmoji = "🌌",
            unlockConditionDesc = "Completes 10 Work tasks & Level 5+",
            isXpMilestone = false,
            xpRequired = 800,
            categoryRequired = "Work",
            completionCountReq = 10
        )
    )

    fun checkIsUnlocked(
        item: GamificationUnlockable,
        totalXp: Int,
        categoryCounts: Map<String, Int>,
        maxStreak: Int
    ): Boolean {
        if (item.isXpMilestone) {
            val levelRequired = when (item.id) {
                "neo_novice" -> 2
                "quantum_architect" -> 3
                "hyperion_voyager" -> 5
                else -> 0
            }
            if (levelRequired > 0) {
                val neededXp = Math.pow((levelRequired - 1.0) / 0.05, 2.0).toInt()
                return totalXp >= neededXp
            }
            return totalXp >= item.xpRequired
        }
        if (item.categoryRequired != null) {
            val countCompleted = categoryCounts[item.categoryRequired] ?: 0
            val meetsCount = countCompleted >= item.completionCountReq
            val levelRequired = if (item.id == "grandmaster_ai") 5 else 0
            val meetsXp = if (levelRequired > 0) {
                val neededXp = Math.pow((levelRequired - 1.0) / 0.05, 2.0).toInt()
                totalXp >= neededXp
            } else {
                totalXp >= item.xpRequired
            }
            return meetsCount && meetsXp
        }
        if (item.streakRequired > 0) {
            return maxStreak >= item.streakRequired
        }
        return false
    }
}
