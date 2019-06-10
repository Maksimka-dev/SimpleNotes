package com.epam.training.simplenotes.util

import android.graphics.Bitmap

interface ImageLoader {
    fun upload(
        imageBitmap: Bitmap,
        toUrl: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun download(
        fromUrl: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    )
}