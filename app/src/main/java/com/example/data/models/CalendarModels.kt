package com.example.data.models

enum class CalendarRangeType {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Data structure representing the frontend's API request payload for calendar metrics.
 */
data class CalendarRequest(
    val rangeType: String, // "DAILY" | "WEEKLY" | "MONTHLY" | "YEARLY"
    val targetDate: String, // ISO-8601 string, e.g., "2026-05-31"
    val categories: List<String> = listOf("Work", "Personal", "Entertainment")
)

/**
 * Breakdown of task counts or completions per category.
 */
data class CategoryBreakdown(
    val work: Int = 0,
    val personal: Int = 0,
    val entertainment: Int = 0
)

/**
 * A single data point in a time-series query, corresponding to a single day, week, month, etc.
 */
data class CalendarTimeSeriesItem(
    val label: String, // e.g., "May 31" / "Week 21" / "Jan"
    val timestamp: Long, // Start millis of the period
    val completedCount: Int,
    val totalCount: Int,
    val xpEarned: Int,
    val categoryBreakdown: CategoryBreakdown
)

/**
 * Response payload carrying full metrics ready for presentation on charts and lists.
 */
data class CalendarResponse(
    val rangeType: String,
    val startDate: String,
    val endDate: String,
    val totalTasks: Int,
    val completedTasks: Int,
    val completionRate: Float, // 0.0 to 100.0
    val totalXpEarned: Int,
    val timeSeriesData: List<CalendarTimeSeriesItem>,
    val categoryDistribution: CategoryBreakdown
)

/**
 * Helper to generate mockup JSON configurations to showcase frontend integration.
 */
object CalendarJsonTemplates {
    fun getSampleRequestJson(type: CalendarRangeType): String {
        val dateStr = "2026-05-31"
        return """
{
  "rangeType": "${type.name}",
  "targetDate": "$dateStr",
  "categories": ["Work", "Personal", "Entertainment"]
}
        """.trimIndent()
    }

    fun getSampleResponseJson(type: CalendarRangeType): String {
        return when (type) {
            CalendarRangeType.DAILY -> """
{
  "rangeType": "DAILY",
  "startDate": "2026-05-31",
  "endDate": "2026-05-31",
  "totalTasks": 5,
  "completedTasks": 3,
  "completionRate": 60.0,
  "totalXpEarned": 45,
  "timeSeriesData": [
    {
      "label": "Morning",
      "timestamp": 1779753600000,
      "completedCount": 1,
      "totalCount": 2,
      "xpEarned": 10,
      "categoryBreakdown": { "work": 1, "personal": 0, "entertainment": 0 }
    },
    {
      "label": "Afternoon",
      "timestamp": 1779775200000,
      "completedCount": 2,
      "totalCount": 2,
      "xpEarned": 25,
      "categoryBreakdown": { "work": 1, "personal": 1, "entertainment": 0 }
    },
    {
      "label": "Evening",
      "timestamp": 1779796800000,
      "completedCount": 0,
      "totalCount": 1,
      "xpEarned": 0,
      "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 }
    }
  ],
  "categoryDistribution": {
    "work": 2,
    "personal": 1,
    "entertainment": 0
  }
}
            """.trimIndent()

            CalendarRangeType.WEEKLY -> """
{
  "rangeType": "WEEKLY",
  "startDate": "2026-05-25",
  "endDate": "2026-05-31",
  "totalTasks": 28,
  "completedTasks": 21,
  "completionRate": 75.0,
  "totalXpEarned": 230,
  "timeSeriesData": [
    { "label": "Mon 25", "timestamp": 1779148800000, "completedCount": 3, "totalCount": 4, "xpEarned": 30, "categoryBreakdown": { "work": 2, "personal": 1, "entertainment": 0 } },
    { "label": "Tue 26", "timestamp": 1779235200000, "completedCount": 4, "totalCount": 4, "xpEarned": 40, "categoryBreakdown": { "work": 2, "personal": 1, "entertainment": 1 } },
    { "label": "Wed 27", "timestamp": 1779321600000, "completedCount": 2, "totalCount": 4, "xpEarned": 20, "categoryBreakdown": { "work": 1, "personal": 1, "entertainment": 0 } },
    { "label": "Thu 28", "timestamp": 1779408000000, "completedCount": 3, "totalCount": 4, "xpEarned": 35, "categoryBreakdown": { "work": 1, "personal": 1, "entertainment": 1 } },
    { "label": "Fri 29", "timestamp": 1779494400000, "completedCount": 4, "totalCount": 4, "xpEarned": 45, "categoryBreakdown": { "work": 2, "personal": 1, "entertainment": 1 } },
    { "label": "Sat 30", "timestamp": 1779580800000, "completedCount": 2, "totalCount": 4, "xpEarned": 30, "categoryBreakdown": { "work": 0, "personal": 1, "entertainment": 1 } },
    { "label": "Sun 31", "timestamp": 1779667200000, "completedCount": 3, "totalCount": 4, "xpEarned": 30, "categoryBreakdown": { "work": 1, "personal": 1, "entertainment": 1 } }
  ],
  "categoryDistribution": {
    "work": 9,
    "personal": 7,
    "entertainment": 5
  }
}
            """.trimIndent()

            CalendarRangeType.MONTHLY -> """
{
  "rangeType": "MONTHLY",
  "startDate": "2026-05-01",
  "endDate": "2026-05-31",
  "totalTasks": 120,
  "completedTasks": 85,
  "completionRate": 70.8,
  "totalXpEarned": 920,
  "timeSeriesData": [
    { "label": "W1 (May 1-7)", "timestamp": 1777075200000, "completedCount": 18, "totalCount": 25, "xpEarned": 190, "categoryBreakdown": { "work": 8, "personal": 6, "entertainment": 4 } },
    { "label": "W2 (May 8-14)", "timestamp": 1777680000000, "completedCount": 22, "totalCount": 28, "xpEarned": 240, "categoryBreakdown": { "work": 10, "personal": 8, "entertainment": 4 } },
    { "label": "W3 (May 15-21)", "timestamp": 1778284800000, "completedCount": 19, "totalCount": 30, "xpEarned": 210, "categoryBreakdown": { "work": 7, "personal": 7, "entertainment": 5 } },
    { "label": "W4 (May 22-28)", "timestamp": 1778889600000, "completedCount": 16, "totalCount": 22, "xpEarned": 180, "categoryBreakdown": { "work": 6, "personal": 6, "entertainment": 4 } },
    { "label": "W5 (May 29-31)", "timestamp": 1779494400000, "completedCount": 10, "totalCount": 15, "xpEarned": 100, "categoryBreakdown": { "work": 4, "personal": 4, "entertainment": 2 } }
  ],
  "categoryDistribution": {
    "work": 35,
    "personal": 31,
    "entertainment": 19
  }
}
            """.trimIndent()

            CalendarRangeType.YEARLY -> """
{
  "rangeType": "YEARLY",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "totalTasks": 1460,
  "completedTasks": 1100,
  "completionRate": 75.3,
  "totalXpEarned": 12500,
  "timeSeriesData": [
    { "label": "Jan", "timestamp": 1767225600000, "completedCount": 90, "totalCount": 120, "xpEarned": 1020, "categoryBreakdown": { "work": 40, "personal": 30, "entertainment": 20 } },
    { "label": "Feb", "timestamp": 1769904000000, "completedCount": 85, "totalCount": 110, "xpEarned": 980, "categoryBreakdown": { "work": 35, "personal": 30, "entertainment": 20 } },
    { "label": "Mar", "timestamp": 1772323200000, "completedCount": 98, "totalCount": 130, "xpEarned": 1100, "categoryBreakdown": { "work": 45, "personal": 35, "entertainment": 18 } },
    { "label": "Apr", "timestamp": 1775001600000, "completedCount": 105, "totalCount": 140, "xpEarned": 1200, "categoryBreakdown": { "work": 50, "personal": 35, "entertainment": 20 } },
    { "label": "May", "timestamp": 1777593600000, "completedCount": 112, "totalCount": 145, "xpEarned": 1310, "categoryBreakdown": { "work": 52, "personal": 40, "entertainment": 20 } },
    { "label": "Jun", "timestamp": 1780272000000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Jul", "timestamp": 1782864000000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Aug", "timestamp": 1785542400000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Sep", "timestamp": 1788220800000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Oct", "timestamp": 1790812800000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Nov", "timestamp": 1793491200000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } },
    { "label": "Dec", "timestamp": 1796083200000, "completedCount": 0, "totalCount": 0, "xpEarned": 0, "categoryBreakdown": { "work": 0, "personal": 0, "entertainment": 0 } }
  ],
  "categoryDistribution": {
    "work": 222,
    "personal": 170,
    "entertainment": 98
  }
}
            """.trimIndent()
        }
    }
}
