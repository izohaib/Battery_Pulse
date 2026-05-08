package com.example.battery_pulse.core.navigation

sealed class Screen(val route: String, val showTopAppBar: Boolean = true, val showBottomNavBar: Boolean = true) {
    object Home : Screen(route = "battery_screen")
    object Display : Screen(route = "display_screen")

    object Setting : Screen(route = "setting_screen", showTopAppBar = false, showBottomNavBar = false)
    object History : Screen(route = "history_screen")
    object AppUsage : Screen(route = "app_usage_screen")
    object About : Screen(route = "about_screen", showTopAppBar = false, showBottomNavBar = false)

}