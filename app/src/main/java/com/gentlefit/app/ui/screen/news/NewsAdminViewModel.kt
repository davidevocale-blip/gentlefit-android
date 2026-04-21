package com.gentlefit.app.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlefit.app.domain.model.NewsArticle
import com.gentlefit.app.domain.model.NewsCategory
import com.gentlefit.app.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewsAdminViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val allNews: StateFlow<List<NewsArticle>> = newsRepository.getAllNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createArticle(title: String, summary: String, content: String, category: NewsCategory) {
        viewModelScope.launch {
            newsRepository.insertNews(NewsArticle(
                title = title, summary = summary, content = content,
                category = category, publishedDate = LocalDate.now().toString()
            ))
        }
    }

    fun updateArticle(article: NewsArticle) {
        viewModelScope.launch { newsRepository.updateArticle(article) }
    }

    fun deleteArticle(id: Long) {
        viewModelScope.launch { newsRepository.deleteArticle(id) }
    }
}
