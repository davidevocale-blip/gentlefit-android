package com.voicetasker.app.domain.repository

import com.voicetasker.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for category operations.
 */
interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryById(categoryId: Long): Flow<Category?>
    suspend fun getCategoryByIdOnce(categoryId: Long): Category?
    suspend fun getCategoryByName(name: String): Category?
    suspend fun getCategoryCount(): Int
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long): Boolean
}
