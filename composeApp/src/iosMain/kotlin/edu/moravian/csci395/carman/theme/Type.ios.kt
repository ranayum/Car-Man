package edu.moravian.csci395.carman.theme

import androidx.compose.material3.Typography
import platform.UIKit.UIFont

actual val AppTypography: Typography = run {
    @Suppress("UNUSED_VARIABLE")
    val warmUp = UIFont.systemFontOfSize(14.0)
    Typography()
}
