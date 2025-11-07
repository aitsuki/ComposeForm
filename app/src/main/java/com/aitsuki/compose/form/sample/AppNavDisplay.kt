package com.aitsuki.compose.form.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.aitsuki.compose.form.sample.screens.HomeScreen
import com.aitsuki.compose.form.sample.screens.LoginScreen
import com.aitsuki.compose.form.sample.screens.RegisterScreen
import com.aitsuki.compose.form.sample.screens.StartScreen

object Routes {
    data object Start
    data object Login
    data object Register
    data object Home
}

val LocalBackStack =
    staticCompositionLocalOf<MutableList<Any>> { error("No BackStack composition") }

@Composable
fun AppNavDisplay() {
    val backStack = remember { mutableStateListOf<Any>(Routes.Start) }
    CompositionLocalProvider(LocalBackStack provides backStack) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is Routes.Start -> NavEntry(key) {
                        StartScreen()
                    }

                    is Routes.Login -> NavEntry(key) {
                        LoginScreen()
                    }

                    is Routes.Register -> NavEntry(key) {
                        RegisterScreen()
                    }

                    is Routes.Home -> NavEntry(key) {
                        HomeScreen()
                    }

                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}