package edu.moravian.csci395.carman

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraLauncher(onPhotoTaken: (String?) -> Unit): () -> Unit = { }
