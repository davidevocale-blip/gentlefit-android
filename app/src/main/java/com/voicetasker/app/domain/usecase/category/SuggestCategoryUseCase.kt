package com.voicetasker.app.domain.usecase.category

import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Suggests a category based on keywords found in the transcription text.
 * Uses simple keyword matching for on-device categorization.
 */
class SuggestCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    private val keywordMap = mapOf(
        "Lavoro" to listOf(
            "riunione", "meeting", "call", "ufficio", "colleghi", "progetto",
            "deadline", "presentazione", "cliente", "lavoro", "email", "report",
            "task", "team", "manager", "colloquio", "contratto", "fattura"
        ),
        "Salute" to listOf(
            "dottore", "medico", "farmacia", "visita", "palestra", "salute",
            "ricetta", "analisi", "ospedale", "dentista", "terapia", "dieta",
            "allenamento", "yoga", "corsa", "checkup", "vaccino", "fisioterapia"
        ),
        "Personale" to listOf(
            "spesa", "casa", "pulire", "cucinare", "famiglia", "amici",
            "compleanno", "regalo", "vacanza", "viaggio", "cena", "pranzo",
            "festa", "cinema", "hobby", "giardino", "animale", "bambini"
        ),
        "Finanza" to listOf(
            "scadenza", "pagamento", "bolletta", "banca", "mutuo", "affitto",
            "tasse", "rata", "assicurazione", "investimento", "stipendio",
            "budget", "risparmio", "carta", "bonifico", "f24"
        )
    )

    suspend operator fun invoke(transcription: String): Category? {
        val lowerText = transcription.lowercase()
        var bestCategory: String? = null
        var bestScore = 0

        keywordMap.forEach { (categoryName, keywords) ->
            val score = keywords.count { keyword -> lowerText.contains(keyword) }
            if (score > bestScore) {
                bestScore = score
                bestCategory = categoryName
            }
        }

        // Default to "Personale" if no strong match
        val targetName = bestCategory ?: "Personale"
        return categoryRepository.getCategoryByName(targetName)
    }
}
