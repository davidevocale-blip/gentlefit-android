package com.voicetasker.app.domain.model

/**
 * Domain model for a note category.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val isDefault: Boolean = false,
    val createdAt: Long = 0
)
