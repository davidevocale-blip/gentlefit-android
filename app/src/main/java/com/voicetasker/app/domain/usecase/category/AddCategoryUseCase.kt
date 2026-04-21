package com.voicetasker.app.domain.usecase.category

import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.repository.CategoryRepository
import javax.inject.Inject

/** Adds a new category. Returns the inserted category ID. */
class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Long {
        return categoryRepository.insertCategory(category)
    }
}
