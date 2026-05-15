package com.gramasuvidha.portal.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val projectId: Int = 0,
    val name: String,
    val nameKn: String? = null,
    val description: String,
    val descriptionKn: String? = null,
    val budget: Double,
    val progress: Int,
    val status: String,
    val category: String,
    val imageUrl: String? = null,
    val thumbnailImageUrl: String? = null,
    val beforeImageUrl: String? = null,
    val afterImageUrl: String? = null,
    val agencyName: String,
    val agencyNameKn: String? = null,
    val startDate: String,
    val expectedCompletionDate: String,
    val statusUpdatesJson: String = "[]",
    val adminId: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

data class ProjectWithRating(
    @Embedded val project: ProjectEntity,
    val averageRating: Double?
)
