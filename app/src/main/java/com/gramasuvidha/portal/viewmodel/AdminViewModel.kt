package com.gramasuvidha.portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gramasuvidha.portal.data.entity.FeedbackEntity
import com.gramasuvidha.portal.data.entity.ProjectEntity
import com.gramasuvidha.portal.data.repository.FeedbackRepository
import com.gramasuvidha.portal.data.repository.ProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AdminViewModel(
    private val projectRepo: ProjectRepository,
    private val feedbackRepo: FeedbackRepository
) : ViewModel() {

    val allProjects: StateFlow<List<ProjectEntity>> = projectRepo.allProjects.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allFeedback: StateFlow<List<FeedbackEntity>> = feedbackRepo.allFeedback.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val totalProjects: StateFlow<Int> = allProjects.map { it.size }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    class Factory(
        private val projectRepo: ProjectRepository,
        private val feedbackRepo: FeedbackRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                return AdminViewModel(projectRepo, feedbackRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
