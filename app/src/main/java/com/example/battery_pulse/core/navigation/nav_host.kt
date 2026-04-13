package com.example.battery_pulse.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.battery_pulse.feature.app_usage.presentation.AppUsageScreen
import com.example.battery_pulse.feature.battery.presentaion.BatteryScreen
import com.example.battery_pulse.feature.battery.presentaion.BatteryViewModel
import com.example.battery_pulse.feature.history.presentaion.HistoryScreen
import com.example.battery_pulse.feature.on_display.presentation.DisplayScreen
import com.example.battery_pulse.feature.setting.presentation.SettingScreen

@Composable
fun AppNavHost(navController: NavHostController, viewModel: BatteryViewModel, modifier: Modifier) {

    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(route = Screen.Home.route) {
            BatteryScreen(navController = navController, viewModel)
        }
        composable(route = Screen.Display.route) {
            DisplayScreen(navController = navController)
        }
        composable(route = Screen.AppUsage.route) {
            AppUsageScreen(navController = navController)
        }
        composable(route = Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(route = Screen.Setting.route) {
            SettingScreen(navController = navController)
        }


//        composable(
//            route = Screen.Detail.route + "?text={text}",
//            arguments = listOf(
//                navArgument("text") {
//                    type = NavType.StringType
//                    nullable = true
//                }
//            )
//        ) {
//            DetailScreen(text = it.arguments?.getString("text"))
//        }
    }
}