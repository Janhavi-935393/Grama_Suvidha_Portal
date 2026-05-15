package com.gramasuvidha.portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gramasuvidha.portal.data.entity.FeedbackEntity
import com.gramasuvidha.portal.data.repository.FeedbackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedbackViewModel(private val repository: FeedbackRepository) : ViewModel() {

    private val _currentProjectId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val feedbackForProject: StateFlow<List<FeedbackEntity>> = _currentProjectId
        .flatMapLatest { projectId ->
            if (projectId == null) flowOf(emptyList())
            else repository.getFeedbackForProject(projectId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAverageRatingFlow(projectId: Int): Flow<Double?> = repository.getAverageRating(projectId)

    fun setProjectId(projectId: Int) {
        _currentProjectId.value = projectId
    }

    // Keep this for compatibility if needed, but returning a Flow and using remember in UI is safer
    fun getFeedbackForProject(projectId: Int): Flow<List<FeedbackEntity>> = 
        repository.getFeedbackForProject(projectId)

    private val _submitSuccess = MutableSharedFlow<Boolean>()
    val submitSuccess = _submitSuccess.asSharedFlow()

    fun submitFeedback(projectId: Int, rating: Int?, comment: String, isIssue: Boolean) {
        viewModelScope.launch {
            val feedback = FeedbackEntity(
                projectId = projectId,
                rating = rating ?: 0,
                comment = comment,
                isIssue = isIssue,
                date = System.currentTimeMillis()
            )
            repository.insertFeedback(feedback)
            _submitSuccess.emit(true)
        }
    }

    class Factory(private val repository: FeedbackRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
                return FeedbackViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
