package com.smartbusiness.expensetracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartbusiness.expensetracker.ui.screens.ExpenseEntryScreen
import com.smartbusiness.expensetracker.ui.screens.ExpenseListScreen
import com.smartbusiness.expensetracker.ui.screens.ExpenseReportScreen
import com.smartbusiness.expensetracker.ui.screens.MainScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main
    ) {
        composable(Screen.Main) {
            MainScreen(navController = navController)
        }

        composable(Screen.ExpenseEntry) {
            ExpenseEntryScreen(navController = navController)
        }

        composable(Screen.ExpenseList) {
            ExpenseListScreen(navController = navController)
        }

        composable(Screen.ExpenseReport) {
            ExpenseReportScreen(navController = navController)
        }
    }
}
