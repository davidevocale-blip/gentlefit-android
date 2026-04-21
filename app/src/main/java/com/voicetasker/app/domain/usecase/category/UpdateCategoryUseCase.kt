package com.voicetasker.app.domain.usecase.category

import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.repository.CategoryRepository
import javax.inject.Inject

/** Updates an existing category. */
class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        categoryRepository.updateCategory(category)
    }
}
