package com.gramasuvidha.portal.data.repository

import com.gramasuvidha.portal.data.db.FeedbackDao
import com.gramasuvidha.portal.data.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

class FeedbackRepository(private val feedbackDao: FeedbackDao) {

    fun getFeedbackForProject(projectId: Int): Flow<List<FeedbackEntity>> = 
        feedbackDao.getFeedbackForProject(projectId)

    val allFeedback: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedback()

    suspend fun insertFeedback(feedback: FeedbackEntity) = feedbackDao.insertFeedback(feedback)

    fun getAverageRating(projectId: Int): Flow<Double?> = feedbackDao.getAverageRating(projectId)
}
