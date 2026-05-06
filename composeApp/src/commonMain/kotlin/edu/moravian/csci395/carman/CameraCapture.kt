package edu.moravian.csci395.carman

import androidx.compose.runtime.Composable

/** Returns a lambda that launches the camera. Calls [onPhotoTake] with the file path, or null on cancel. */
@Composable
expect fun rememberCameraLauncher(onPhotoTake: (String?) -> Unit): () -> Unit
