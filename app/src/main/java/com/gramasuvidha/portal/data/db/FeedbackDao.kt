package com.gramasuvidha.portal.data.db

import androidx.room.*
import com.gramasuvidha.portal.data.entity.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface FeedbackDao {
    @Query("SELECT * FROM feedback WHERE projectId = :projectId ORDER BY date DESC")
    fun getFeedbackForProject(projectId: Int): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedback ORDER BY date DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT AVG(rating) FROM feedback WHERE projectId = :projectId AND rating > 0")
    fun getAverageRating(projectId: Int): Flow<Double?>
}
