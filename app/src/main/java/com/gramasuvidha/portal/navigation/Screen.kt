package com.gramasuvidha.portal.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object AuthSelection : Screen("auth_selection")
    object CitizenLogin : Screen("citizen_login")
    object CitizenRegistration : Screen("citizen_registration")
    object AdminLogin : Screen("admin_login")
    object AdminRegistration : Screen("admin_registration")
    object CitizenHome : Screen("citizen_home")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Int) = "project_detail/$projectId"
    }
    object AdminDashboard : Screen("admin_dashboard")
    object AddEditProject : Screen("add_edit_project?projectId={projectId}") {
        fun createRoute(projectId: Int?) = if (projectId != null) "add_edit_project?projectId=$projectId" else "add_edit_project"
    }
    object FeedbackReports : Screen("feedback_reports")
}
