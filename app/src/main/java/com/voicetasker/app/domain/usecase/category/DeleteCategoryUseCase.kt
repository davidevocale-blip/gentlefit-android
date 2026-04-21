package com.voicetasker.app.domain.usecase.category

import com.voicetasker.app.domain.repository.CategoryRepository
import javax.inject.Inject

/** Deletes a category (only non-default). Returns true if successful. */
class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Long): Boolean {
        return categoryRepository.deleteCategory(categoryId)
    }
}
