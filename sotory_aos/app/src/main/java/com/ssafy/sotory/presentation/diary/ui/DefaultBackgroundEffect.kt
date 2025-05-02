package com.ssafy.sotory.presentation.diary.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale


@Composable
fun DefaultBackgroundEffect() {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(AnimatedImageDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("https://assets.codepen.io/13471/sparkles.gif")
            .scale(Scale.FILL)
            .crossfade(true)
            .build(),
        alpha = 0.7f,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        imageLoader = imageLoader,
        modifier = Modifier.fillMaxSize()
    )
}