package com.gramasuvidha.portal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gramasuvidha.portal.R
import com.gramasuvidha.portal.ui.components.ProgressBarColored
import com.gramasuvidha.portal.ui.components.StarRatingBar
import com.gramasuvidha.portal.viewmodel.FeedbackViewModel
import com.gramasuvidha.portal.viewmodel.ProjectViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Int,
    projectViewModel: ProjectViewModel,
    feedbackViewModel: FeedbackViewModel,
    onBack: () -> Unit
) {
    val project by projectViewModel.selectedProject.collectAsState()
    
    // Correctly handle Flow collection by remembering the flow instance
    val feedbackList by remember(projectId) { 
        feedbackViewModel.getFeedbackForProject(projectId) 
    }.collectAsState(initial = emptyList())
    
    val avgRating by remember(projectId) { 
        feedbackViewModel.getAverageRatingFlow(projectId) 
    }.collectAsState(initial = null)
    
    val submitSuccess by feedbackViewModel.submitSuccess.collectAsState(initial = false)

    var selectedRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val feedbackSuccessMessage = stringResource(R.string.feedback_submitted)

    LaunchedEffect(projectId) {
        projectViewModel.loadProjectById(projectId)
    }

    LaunchedEffect(submitSuccess) {
        if (submitSuccess) {
            snackbarHostState.showSnackbar(feedbackSuccessMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = stringResource(R.string.cancel))
                    }
                }
            )
        }
    ) { padding ->
        project?.let { proj ->
            val budgetFormatted = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
                maximumFractionDigits = 0
            }.format(proj.budget)

            val statusRes = when (proj.status) {
                "Completed" -> R.string.status_completed
                "In Progress" -> R.string.status_in_progress
                "Not Started" -> R.string.status_not_started
                else -> null
            }
            
            val statusColor = when (proj.status) {
                "Completed" -> Color(0xFF4CAF50)
                "In Progress" -> Color(0xFFFF9800)
                else -> Color(0xFF9E9E9E)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and Basic Info
                    Text(
                        text = proj.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Budget Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${stringResource(R.string.budget)}: $budgetFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status chip
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = if (statusRes != null) stringResource(statusRes) else proj.status,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Agency row
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = proj.agencyName, style = MaterialTheme.typography.bodyMedium)
                    }

                    // Dates row
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${proj.startDate} to ${proj.expectedCompletionDate}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = proj.description, style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Progress
                    ProgressBarColored(progress = proj.progress, showLabel = true)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status Updates
                    Text(text = stringResource(R.string.status_updates), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    val statusUpdates = remember(proj.statusUpdatesJson) {
                        try {
                            Gson().fromJson<List<Map<String, String>>>(
                                proj.statusUpdatesJson,
                                object : TypeToken<List<Map<String, String>>>() {}.type
                            )
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    if (statusUpdates.isEmpty()) {
                        Text(text = stringResource(R.string.no_updates), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        statusUpdates.forEach { update ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(text = update["date"] ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text(text = update["text"] ?: "", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Feedback section
                    Text(text = stringResource(R.string.citizen_feedback), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        StarRatingBar(rating = avgRating?.toInt() ?: 0, onRatingChanged = {}, isReadOnly = true, starSize = 20.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.ratings_summary, avgRating ?: 0.0, feedbackList.size),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(text = stringResource(R.string.give_feedback), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                    StarRatingBar(rating = selectedRating, onRatingChanged = { selectedRating = it })
                    
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 200) comment = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        label = { Text(stringResource(R.string.description)) },
                        supportingText = { Text("${comment.length}/200", modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)) },
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                feedbackViewModel.submitFeedback(proj.projectId, selectedRating, comment, false)
                                selectedRating = 0
                                comment = ""
                            },
                            enabled = selectedRating > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.submit_rating))
                        }
                        
                        OutlinedButton(
                            onClick = {
                                feedbackViewModel.submitFeedback(proj.projectId, if (selectedRating > 0) selectedRating else null, comment, true)
                                selectedRating = 0
                                comment = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.report_issue))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
