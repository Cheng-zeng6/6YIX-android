package com.yix6.android.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yix6.android.ui.screen.AiInterpretScreen
import com.yix6.android.ui.screen.DivinationScreen
import com.yix6.android.ui.screen.HistoryScreen
import com.yix6.android.ui.screen.HomeScreen
import com.yix6.android.ui.screen.ResultScreen
import com.yix6.android.ui.viewmodel.AppViewModel
import com.yix6.android.ui.viewmodel.DivinationMode

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Divination : Screen("divination/{mode}") {
        fun routeFor(mode: DivinationMode) = "divination/${mode.name}"
    }
    data object Result : Screen("result")
    data object History : Screen("history")
    data object AiInterpret : Screen("ai_interpret/{hexagramName}/{changedName}/{changingLines}") {
        fun routeFor(hexagramName: String, changedName: String?, changingLines: List<Int>): String {
            val encoded = Uri.encode(hexagramName)
            val changedEncoded = Uri.encode(changedName ?: "none")
            val lines = changingLines.joinToString(",").ifEmpty { "none" }
            return "ai_interpret/$encoded/$changedEncoded/$lines"
        }
    }
}

@Composable
fun AppNavigation(appViewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onModeSelected = { mode ->
                    appViewModel.clearSession()
                    navController.navigate(Screen.Divination.routeFor(mode))
                },
                onHistoryClick = { navController.navigate(Screen.History.route) },
            )
        }

        composable(
            route = Screen.Divination.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType }),
        ) { backStackEntry ->
            val modeName = backStackEntry.arguments?.getString("mode") ?: DivinationMode.SHAKE.name
            val mode = DivinationMode.valueOf(modeName)
            DivinationScreen(
                initialMode = mode,
                onComplete = { sixThrows ->
                    appViewModel.setSixThrows(sixThrows)
                    navController.navigate(Screen.Result.route)
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                appViewModel = appViewModel,
                onAiInterpret = { hexagramName, changedName, changingLines ->
                    navController.navigate(
                        Screen.AiInterpret.routeFor(hexagramName, changedName, changingLines)
                    )
                },
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.AiInterpret.route,
            arguments = listOf(
                navArgument("hexagramName") { type = NavType.StringType },
                navArgument("changedName") { type = NavType.StringType },
                navArgument("changingLines") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val hexagramName = Uri.decode(
                backStackEntry.arguments?.getString("hexagramName") ?: ""
            )
            val changedRaw = Uri.decode(
                backStackEntry.arguments?.getString("changedName") ?: "none"
            )
            val changedName = if (changedRaw == "none") null else changedRaw
            val linesRaw = backStackEntry.arguments?.getString("changingLines") ?: "none"
            val changingLines = if (linesRaw == "none") emptyList()
            else linesRaw.split(",").mapNotNull { it.trim().toIntOrNull() }

            AiInterpretScreen(
                hexagramName = hexagramName,
                changedName = changedName,
                changingLines = changingLines,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
