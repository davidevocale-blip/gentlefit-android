package com.voicetasker.app.domain.model

/**
 * Domain model for a reminder.
 */
data class Reminder(
    val id: Long = 0,
    val noteId: Long,
    val triggerAt: Long,
    val type: ReminderType,
    val isTriggered: Boolean = false,
    val workRequestId: String = ""
)

/**
 * Available reminder types with their offset from the event time.
 */
enum class ReminderType(val label: String, val offsetMillis: Long) {
    ONE_DAY("1 giorno prima", 24 * 60 * 60 * 1000L),
    TWELVE_HOURS("12 ore prima", 12 * 60 * 60 * 1000L),
    TWO_HOURS("2 ore prima", 2 * 60 * 60 * 1000L);

    companion object {
        fun fromString(value: String): ReminderType = when (value) {
            "1_DAY" -> ONE_DAY
            "12_HOURS" -> TWELVE_HOURS
            "2_HOURS" -> TWO_HOURS
            else -> TWO_HOURS
        }
    }

    fun toDbString(): String = when (this) {
        ONE_DAY -> "1_DAY"
        TWELVE_HOURS -> "12_HOURS"
        TWO_HOURS -> "2_HOURS"
    }
}
