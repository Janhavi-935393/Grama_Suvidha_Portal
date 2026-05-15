package com.gramasuvidha.portal.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R
import com.gramasuvidha.portal.ui.components.OfflineBanner
import com.gramasuvidha.portal.ui.components.ProjectCard
import com.gramasuvidha.portal.viewmodel.FeedbackViewModel
import com.gramasuvidha.portal.viewmodel.LanguageViewModel
import com.gramasuvidha.portal.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHomeScreen(
    onProjectClick: (Int) -> Unit,
    onLogout: () -> Unit,
    projectViewModel: ProjectViewModel,
    feedbackViewModel: FeedbackViewModel,
    languageViewModel: LanguageViewModel
) {
    // Use the optimized projectsWithRatings StateFlow to avoid per-item database queries
    val projectsWithRatings by projectViewModel.projectsWithRatings.collectAsState()
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkConnection(context)) }

    // Periodically check connection
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = checkConnection(context)
            kotlinx.coroutines.delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { languageViewModel.toggleLanguage() }) {
                        Icon(Icons.Default.Language, contentDescription = stringResource(R.string.language_toggle))
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!isOnline) {
                OfflineBanner()
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Using the combined model which includes averageRating
                items(projectsWithRatings, key = { it.project.projectId }) { item ->
                    ProjectCard(
                        project = item.project,
                        avgRating = item.averageRating,
                        onClick = { onProjectClick(item.project.projectId) }
                    )
                }
            }
        }
    }
}

private fun checkConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
