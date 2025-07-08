package com.lowbudgetlcs.dto.lol

enum class TeamType(val code: Int) {
    SUBTEAM(0),
    BLUE(100),
    RED(200),
    AI(300);

    companion object {
        fun fromCode(value: String): TeamType? {
            val cleanedValue = value.substringBefore(".") // Removes decimal part if present
            return fromId(cleanedValue.toIntOrNull() ?: return null)
        }

        fun fromId(teamId: Int): TeamType? {
            return values().find { it.code == teamId }
        }
    }

    fun opposite(): TeamType? = when (this) {
        RED -> BLUE
        BLUE -> RED
        AI -> AI
        else -> null
    }

    fun prettyName(): String = when (this) {
        RED -> "Red"
        BLUE -> "Blue"
        AI -> "AI"
        else -> "This enum does not have a pretty name"
    }

    fun getValue(): Int = code
}