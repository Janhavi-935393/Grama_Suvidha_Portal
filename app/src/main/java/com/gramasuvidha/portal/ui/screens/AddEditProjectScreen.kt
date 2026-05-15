package com.gramasuvidha.portal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R
import com.gramasuvidha.portal.data.entity.ProjectEntity
import com.gramasuvidha.portal.viewmodel.ProjectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProjectScreen(
    projectId: Int? = null,
    projectViewModel: ProjectViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val projectToEdit by projectViewModel.selectedProject.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    var status by remember { mutableStateOf("Not Started") }
    var category by remember { mutableStateOf("Road") }
    var agency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var expectedCompletionDate by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }
    var budgetError by remember { mutableStateOf(false) }
    var agencyError by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        if (projectId != null) {
            projectViewModel.loadProjectById(projectId)
        }
    }

    LaunchedEffect(projectToEdit) {
        if (projectId != null && projectToEdit?.projectId == projectId) {
            projectToEdit?.let {
                name = it.name
                description = it.description
                budget = it.budget.toString()
                progress = it.progress.toFloat()
                status = it.status
                category = it.category
                agency = it.agencyName
                startDate = it.startDate
                expectedCompletionDate = it.expectedCompletionDate
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (projectId == null) stringResource(R.string.add_project) else stringResource(R.string.edit_project)) },
                navigationIcon = {
                    IconButton(onClick = onCancel, enabled = !isSaving) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = it.isBlank() },
                    label = { Text(stringResource(R.string.project_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = { if (nameError) Text(stringResource(R.string.required), color = MaterialTheme.colorScheme.error) },
                    enabled = !isSaving
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; descError = it.isBlank() },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    isError = descError,
                    supportingText = { if (descError) Text(stringResource(R.string.required), color = MaterialTheme.colorScheme.error) },
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it; budgetError = it.isBlank() },
                    label = { Text(stringResource(R.string.budget)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = budgetError,
                    supportingText = { if (budgetError) Text(stringResource(R.string.required), color = MaterialTheme.colorScheme.error) },
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(12.dp))

                val categoryOptions = listOf(
                    "Road" to stringResource(R.string.cat_road),
                    "Water" to stringResource(R.string.cat_water),
                    "Community" to stringResource(R.string.cat_community),
                    "Health" to stringResource(R.string.cat_health),
                    "Education" to stringResource(R.string.cat_education)
                )

                ExposedDropdownField(
                    label = stringResource(R.string.category),
                    options = categoryOptions,
                    selectedInternal = category,
                    onOptionSelected = { category = it },
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(12.dp))

                val statusOptions = listOf(
                    "Not Started" to stringResource(R.string.status_not_started),
                    "In Progress" to stringResource(R.string.status_in_progress),
                    "Completed" to stringResource(R.string.status_completed)
                )

                ExposedDropdownField(
                    label = stringResource(R.string.status),
                    options = statusOptions,
                    selectedInternal = status,
                    onOptionSelected = { status = it },
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(R.string.progress_value, progress.toInt()), style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..100f,
                    steps = 99,
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = agency,
                    onValueChange = { agency = it; agencyError = it.isBlank() },
                    label = { Text(stringResource(R.string.agency)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = agencyError,
                    supportingText = { if (agencyError) Text(stringResource(R.string.required), color = MaterialTheme.colorScheme.error) },
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text(stringResource(R.string.start_date)) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("DD/MM/YYYY") },
                        enabled = !isSaving
                    )
                    OutlinedTextField(
                        value = expectedCompletionDate,
                        onValueChange = { expectedCompletionDate = it },
                        label = { Text(stringResource(R.string.end_date)) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("DD/MM/YYYY") },
                        enabled = !isSaving
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), enabled = !isSaving) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            nameError = name.isBlank()
                            descError = description.isBlank()
                            budgetError = budget.isBlank()
                            agencyError = agency.isBlank()

                            if (!nameError && !descError && !budgetError && !agencyError) {
                                isSaving = true
                                scope.launch {
                                    // Preserve existing entity data if editing
                                    val newProject = projectToEdit?.copy(
                                        name = name,
                                        description = description,
                                        budget = budget.toDoubleOrNull() ?: 0.0,
                                        progress = progress.toInt(),
                                        status = status,
                                        category = category,
                                        agencyName = agency,
                                        startDate = startDate,
                                        expectedCompletionDate = expectedCompletionDate,
                                        imageUrl = null,
                                        thumbnailImageUrl = null,
                                        beforeImageUrl = null,
                                        afterImageUrl = null
                                    ) ?: ProjectEntity(
                                        name = name,
                                        description = description,
                                        budget = budget.toDoubleOrNull() ?: 0.0,
                                        progress = progress.toInt(),
                                        status = status,
                                        category = category,
                                        agencyName = agency,
                                        startDate = startDate,
                                        expectedCompletionDate = expectedCompletionDate,
                                        imageUrl = null,
                                        thumbnailImageUrl = null
                                    )
                                    
                                    if (projectId == null) {
                                        projectViewModel.addProject(newProject)
                                    } else {
                                        projectViewModel.updateProject(newProject)
                                    }
                                    
                                    withContext(Dispatchers.Main) {
                                        isSaving = false
                                        onSave()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.save_project))
                        }
                    }
                }
            }
            
            if (isSaving) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.1f)
                ) {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownField(
    label: String,
    options: List<Pair<String, String>>, // Pair<InternalValue, DisplayValue>
    selectedInternal: String,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = options.find { it.first == selectedInternal }?.second ?: selectedInternal

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        onOptionSelected(option.first)
                        expanded = false
                    }
                )
            }
        }
    }
}
