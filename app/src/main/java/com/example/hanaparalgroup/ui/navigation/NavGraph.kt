package com.example.hanaparalgroup.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hanaparalgroup.ui.screens.*

object Routes {
    const val SPLASH          = "splash"
    const val LOGIN           = "login"
    const val DASHBOARD       = "dashboard"
    const val PROFILE         = "profile"
    const val PROFILE_EDIT    = "profile_edit"
    const val GROUPS          = "groups"
    const val GROUP_DETAIL    = "group_detail/{groupId}"
    const val CREATE_GROUP    = "create_group"
    const val NOTIFICATIONS   = "notifications"
    const val SUPERUSER       = "superuser"
    const val BIOMETRIC_GATE  = "biometric_gate"

    fun groupDetail(groupId: String) = "group_detail/$groupId"
}

@Composable
fun HanapAralNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin     = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToProfile       = { navController.navigate(Routes.PROFILE) },
                onNavigateToGroups        = { navController.navigate(Routes.GROUPS) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToSuperuser     = { navController.navigate(Routes.BIOMETRIC_GATE) },
                onNavigateToCreateGroup   = { navController.navigate(Routes.CREATE_GROUP) },
                onNavigateToGroupDetail   = { id -> navController.navigate(Routes.groupDetail(id)) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack   = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.PROFILE_EDIT) },
                onSignOut        = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE_EDIT) {
            ProfileEditScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaved        = { navController.popBackStack() }
            )
        }

        composable(Routes.GROUPS) {
            GroupsScreen(
                onNavigateBack      = { navController.popBackStack() },
                onNavigateToCreate  = { navController.navigate(Routes.CREATE_GROUP) },
                onNavigateToDetail  = { id -> navController.navigate(Routes.groupDetail(id)) }
            )
        }

        composable(Routes.GROUP_DETAIL) { backStack ->
            val groupId = backStack.arguments?.getString("groupId") ?: ""
            GroupDetailScreen(
                groupId        = groupId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onNavigateBack  = { navController.popBackStack() },
                onGroupCreated  = { navController.popBackStack() }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BIOMETRIC_GATE) {
            BiometricGateScreen(
                onAuthSuccess  = {
                    navController.navigate(Routes.SUPERUSER) {
                        popUpTo(Routes.BIOMETRIC_GATE) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SUPERUSER) {
            SuperuserScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}