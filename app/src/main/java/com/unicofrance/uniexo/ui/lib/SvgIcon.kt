package com.unicofrance.uniexo.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder

@Composable
fun SvgIcon(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    var isLoader by remember { mutableStateOf(false) }

    val painter = rememberAsyncImagePainter(
        model = url,
        imageLoader = imageLoader,
        onState = { state ->
            if (state is AsyncImagePainter.State.Success)
                isLoader = true
        }
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )

    if (painter.state is AsyncImagePainter.State.Loading) {
        CircularProgressIndicator(
            modifier = modifier
        )
    }
}