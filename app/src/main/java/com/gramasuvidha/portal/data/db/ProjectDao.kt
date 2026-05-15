package com.gramasuvidha.portal.data.db

import androidx.room.*
import com.gramasuvidha.portal.data.entity.ProjectEntity
import com.gramasuvidha.portal.data.entity.ProjectWithRating
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("""
        SELECT projects.*, AVG(feedback.rating) as averageRating 
        FROM projects 
        LEFT JOIN feedback ON projects.projectId = feedback.projectId 
        GROUP BY projects.projectId 
        ORDER BY projects.createdAt DESC
    """)
    fun getAllProjectsWithRatings(): Flow<List<ProjectWithRating>>

    @Query("SELECT * FROM projects WHERE projectId = :id")
    suspend fun getProjectById(id: Int): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity): Int

    @Delete
    suspend fun deleteProject(project: ProjectEntity): Int

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int
}
