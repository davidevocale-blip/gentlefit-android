package com.voicetasker.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a note category.
 * Default categories (isDefault=true) cannot be deleted by the user.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,             // e.g. "#6C63FF"
    val iconName: String,             // Material icon name e.g. "Work", "FitnessCenter"
    val isDefault: Boolean = false,   // default categories cannot be deleted
    val createdAt: Long
)
