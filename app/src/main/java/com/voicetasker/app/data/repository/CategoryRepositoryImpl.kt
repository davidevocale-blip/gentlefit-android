package com.voicetasker.app.data.repository

import com.voicetasker.app.data.local.dao.CategoryDao
import com.voicetasker.app.data.local.entity.CategoryEntity
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCategoryById(categoryId: Long): Flow<Category?> {
        return categoryDao.getCategoryById(categoryId).map { it?.toDomain() }
    }

    override suspend fun getCategoryByIdOnce(categoryId: Long): Category? {
        return categoryDao.getCategoryByIdOnce(categoryId)?.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun getCategoryCount(): Int {
        return categoryDao.getCategoryCount()
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(categoryId: Long): Boolean {
        return categoryDao.deleteCategoryById(categoryId) > 0
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id, name = name, colorHex = colorHex,
        iconName = iconName, isDefault = isDefault, createdAt = createdAt
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id, name = name, colorHex = colorHex,
        iconName = iconName, isDefault = isDefault, createdAt = createdAt
    )
}
