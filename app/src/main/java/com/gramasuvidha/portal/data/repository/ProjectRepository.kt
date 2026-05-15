package com.gramasuvidha.portal.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gramasuvidha.portal.data.db.ProjectDao
import com.gramasuvidha.portal.data.entity.ProjectEntity
import com.gramasuvidha.portal.data.entity.ProjectWithRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

class ProjectRepository(private val projectDao: ProjectDao, private val context: Context) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    
    val allProjectsWithRatings: Flow<List<ProjectWithRating>> = projectDao.getAllProjectsWithRatings()

    suspend fun getProjectById(id: Int): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun insertProject(project: ProjectEntity) = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        if (projectDao.getProjectCount() == 0) {
            try {
                context.assets.open("projects.json").use { inputStream ->
                    inputStream.bufferedReader().use { reader ->
                        val type = object : TypeToken<List<ProjectJsonModel>>() {}.type
                        val projectsJson: List<ProjectJsonModel> = Gson().fromJson(reader, type)
                        
                        val gson = Gson()
                        val projectEntities = projectsJson.map { json ->
                            val budgetValue = json.budget.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
                            ProjectEntity(
                                projectId = json.projectId,
                                name = json.name,
                                nameKn = json.nameKn,
                                description = json.description,
                                descriptionKn = json.descriptionKn,
                                budget = budgetValue,
                                progress = json.progress,
                                status = json.status,
                                category = json.category,
                                imageUrl = json.imageUrl,
                                thumbnailImageUrl = json.imageUrl,
                                beforeImageUrl = json.beforeImageUrl,
                                afterImageUrl = json.afterImageUrl,
                                agencyName = json.agencyName,
                                agencyNameKn = json.agencyNameKn,
                                startDate = json.startDate,
                                expectedCompletionDate = json.expectedCompletionDate,
                                statusUpdatesJson = gson.toJson(json.statusUpdates),
                                adminId = 1
                            )
                        }
                        projectDao.insertProjects(projectEntities)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private data class ProjectJsonModel(
        val projectId: Int,
        val name: String,
        val nameKn: String?,
        val description: String,
        val descriptionKn: String?,
        val budget: String,
        val progress: Int,
        val status: String,
        val category: String,
        val imageUrl: String?,
        val beforeImageUrl: String?,
        val afterImageUrl: String?,
        val agencyName: String,
        val agencyNameKn: String?,
        val startDate: String,
        val expectedCompletionDate: String,
        val statusUpdates: List<StatusUpdateJsonModel>
    )

    private data class StatusUpdateJsonModel(
        val date: String,
        val text: String,
        val textKn: String?
    )
}
