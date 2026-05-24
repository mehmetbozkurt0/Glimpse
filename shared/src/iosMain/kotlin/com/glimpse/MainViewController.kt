package com.glimpse

import androidx.compose.ui.window.ComposeUIViewController
import com.glimpse.data.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DatabaseDriverFactory())
}