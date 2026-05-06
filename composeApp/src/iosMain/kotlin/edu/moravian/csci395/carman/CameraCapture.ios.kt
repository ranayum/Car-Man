package edu.moravian.csci395.carman

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraLauncher(onPhotoTake: (String?) -> Unit): () -> Unit = { }
