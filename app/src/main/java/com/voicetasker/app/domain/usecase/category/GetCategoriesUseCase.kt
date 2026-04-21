package com.voicetasker.app.domain.usecase.category

import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Retrieves all categories. */
class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}
