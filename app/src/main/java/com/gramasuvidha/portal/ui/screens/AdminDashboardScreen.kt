package com.gramasuvidha.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R
import com.gramasuvidha.portal.ui.components.ProgressBarColored
import com.gramasuvidha.portal.viewmodel.AdminViewModel
import com.gramasuvidha.portal.viewmodel.LanguageViewModel
import com.gramasuvidha.portal.viewmodel.ProjectViewModel
import com.gramasuvidha.portal.ui.theme.PrimaryBlue
import com.gramasuvidha.portal.ui.theme.AccentGreen
import com.gramasuvidha.portal.ui.theme.ChipBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    projectViewModel: ProjectViewModel,
    languageViewModel: LanguageViewModel,
    onAddProject: () -> Unit,
    onEditProject: (Int) -> Unit,
    onViewFeedback: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val projects by adminViewModel.allProjects.collectAsState()
    val totalFeedback by adminViewModel.allFeedback.collectAsState()
    val totalProjects by adminViewModel.totalProjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_dashboard)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    IconButton(onClick = { languageViewModel.toggleLanguage() }) {
                        Icon(Icons.Default.Language, contentDescription = stringResource(R.string.language_toggle))
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProject,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_project))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = totalProjects.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.total_projects), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Comment, contentDescription = null, tint = AccentGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = totalFeedback.size.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.feedback), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            TextButton(
                onClick = onViewFeedback,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.view_feedback))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(projects) { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = project.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        
                                        val categoryRes = when (project.category) {
                                            "Road" -> R.string.cat_road
                                            "Water" -> R.string.cat_water
                                            "Community" -> R.string.cat_community
                                            "Health" -> R.string.cat_health
                                            "Education" -> R.string.cat_education
                                            else -> null
                                        }

                                        Surface(
                                            color = ChipBackground,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Text(
                                                text = if (categoryRes != null) stringResource(categoryRes) else project.category,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onEditProject(project.projectId) }) {
                                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_project), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                ProgressBarColored(progress = project.progress, showLabel = false)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val statusRes = when (project.status) {
                                        "Completed" -> R.string.status_completed
                                        "In Progress" -> R.string.status_in_progress
                                        "Not Started" -> R.string.status_not_started
                                        else -> null
                                    }

                                    val statusColor = when (project.status) {
                                        "Completed" -> Color(0xFF4CAF50)
                                        "In Progress" -> Color(0xFFFF9800)
                                        else -> Color(0xFF9E9E9E)
                                    }
                                    Box(modifier = Modifier.size(8.dp).background(statusColor, RoundedCornerShape(4.dp)))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (statusRes != null) stringResource(statusRes) else project.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
