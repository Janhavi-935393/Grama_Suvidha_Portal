package com.gramasuvidha.portal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gramasuvidha.portal.ui.screens.*
import com.gramasuvidha.portal.viewmodel.*
import kotlinx.coroutines.delay

@Composable
fun NavGraph(
    projectViewModel: ProjectViewModel,
    feedbackViewModel: FeedbackViewModel,
    adminViewModel: AdminViewModel,
    languageViewModel: LanguageViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                projectViewModel.seedDatabase()
                delay(2000)
                navController.navigate(Screen.AuthSelection.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            SplashScreen()
        }

        composable(Screen.AuthSelection.route) {
            AuthSelectionScreen(
                onCitizenLogin = { navController.navigate(Screen.CitizenLogin.route) },
                onCitizenRegister = { navController.navigate(Screen.CitizenRegistration.route) },
                onAdminLogin = { navController.navigate(Screen.AdminLogin.route) },
                onAdminRegister = { navController.navigate(Screen.AdminRegistration.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(Screen.CitizenLogin.route) {
            CitizenLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.CitizenHome.route) {
                        popUpTo(Screen.AuthSelection.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CitizenRegistration.route) {
            CitizenRegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.CitizenHome.route) {
                        popUpTo(Screen.AuthSelection.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AuthSelection.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminRegistration.route) {
            AdminRegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AuthSelection.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CitizenHome.route) {
            CitizenHomeScreen(
                onProjectClick = { navController.navigate(Screen.ProjectDetail.createRoute(it)) },
                onLogout = { navController.navigate(Screen.AuthSelection.route) { popUpTo(0) } },
                projectViewModel = projectViewModel,
                feedbackViewModel = feedbackViewModel,
                languageViewModel = languageViewModel
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId") ?: return@composable
            ProjectDetailScreen(
                projectId = projectId,
                projectViewModel = projectViewModel,
                feedbackViewModel = feedbackViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                projectViewModel = projectViewModel,
                languageViewModel = languageViewModel,
                onAddProject = { navController.navigate(Screen.AddEditProject.createRoute(null)) },
                onEditProject = { navController.navigate(Screen.AddEditProject.createRoute(it)) },
                onViewFeedback = { navController.navigate(Screen.FeedbackReports.route) },
                onLogout = { navController.navigate(Screen.AuthSelection.route) { popUpTo(0) } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddEditProject.route,
            arguments = listOf(navArgument("projectId") { 
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId").takeIf { it != -1 }
            AddEditProjectScreen(
                projectId = projectId,
                projectViewModel = projectViewModel,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.FeedbackReports.route) {
            FeedbackReportsScreen(
                adminViewModel = adminViewModel,
                projectViewModel = projectViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
