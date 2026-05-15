package com.gramasuvidha.portal

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gramasuvidha.portal.data.db.AppDatabase
import com.gramasuvidha.portal.data.repository.FeedbackRepository
import com.gramasuvidha.portal.data.repository.ProjectRepository
import com.gramasuvidha.portal.navigation.NavGraph
import com.gramasuvidha.portal.ui.theme.GramaSuvidhaTheme
import com.gramasuvidha.portal.viewmodel.*
import java.util.*

class MainActivity : ComponentActivity() {
    
    // Initialize dependencies outside of composition to prevent ANRs and unnecessary re-initialization
    private val database by lazy { AppDatabase.getDatabase(applicationContext) }
    private val projectRepo by lazy { ProjectRepository(database.projectDao(), applicationContext) }
    private val feedbackRepo by lazy { FeedbackRepository(database.feedbackDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val languageViewModel: LanguageViewModel = viewModel(factory = LanguageViewModel.Factory(applicationContext))
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()

            // When language changes, we update the locale and provide a localized context
            val locale = remember(currentLanguage) { 
                Log.d("MainActivity", "Language changed to: $currentLanguage")
                Locale(currentLanguage) 
            }
            
            val configuration = remember(locale) {
                Configuration(resources.configuration).apply {
                    setLocale(locale)
                }
            }
            
            val localizedContext = remember(configuration) {
                createConfigurationContext(configuration)
            }

            LaunchedEffect(locale) {
                Locale.setDefault(locale)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides configuration,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                LocalOnBackPressedDispatcherOwner provides this@MainActivity
            ) {
                GramaSuvidhaTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Use provided factories with pre-initialized repositories
                        val projectViewModel: ProjectViewModel = viewModel(factory = ProjectViewModel.Factory(projectRepo))
                        val feedbackViewModel: FeedbackViewModel = viewModel(factory = FeedbackViewModel.Factory(feedbackRepo))
                        val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory(projectRepo, feedbackRepo))

                        NavGraph(
                            projectViewModel = projectViewModel,
                            feedbackViewModel = feedbackViewModel,
                            adminViewModel = adminViewModel,
                            languageViewModel = languageViewModel
                        )
                    }
                }
            }
        }
    }
}
