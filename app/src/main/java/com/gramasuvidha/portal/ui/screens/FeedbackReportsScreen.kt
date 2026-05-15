package com.gramasuvidha.portal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R
import com.gramasuvidha.portal.ui.components.FeedbackCard
import com.gramasuvidha.portal.viewmodel.AdminViewModel
import com.gramasuvidha.portal.viewmodel.ProjectViewModel
import com.gramasuvidha.portal.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackReportsScreen(
    adminViewModel: AdminViewModel,
    projectViewModel: ProjectViewModel,
    onBack: () -> Unit
) {
    val allFeedback by adminViewModel.allFeedback.collectAsState()
    val allProjects by projectViewModel.allProjects.collectAsState()

    val groupedFeedback = remember(allFeedback) {
        allFeedback.groupBy { it.projectId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.view_feedback)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (groupedFeedback.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.no_feedback),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedFeedback.forEach { (projectId, feedbackItems) ->
                    item(key = "header_$projectId") {
                        val projectName = allProjects.find { it.projectId == projectId }?.name ?: stringResource(R.string.project_id_label, projectId)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                        ) {
                            Text(
                                text = projectName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                    
                    items(feedbackItems, key = { it.feedbackId }) { feedback ->
                        FeedbackCard(feedback = feedback)
                    }
                }
            }
        }
    }
}
