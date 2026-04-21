package com.voicetasker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.voicetasker.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for note categories.
 */
@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<CategoryEntity?>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryByIdOnce(categoryId: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId AND isDefault = 0")
    suspend fun deleteCategoryById(categoryId: Long): Int
}
