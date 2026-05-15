package com.gramasuvidha.portal.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val feedbackId: Int = 0,
    val projectId: Int,
    val citizenName: String = "Anonymous Citizen",
    val rating: Int,
    val comment: String,
    val isIssue: Boolean = false,
    val date: Long = System.currentTimeMillis()
)
