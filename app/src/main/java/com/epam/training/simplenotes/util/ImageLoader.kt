package com.epam.training.simplenotes.util

import android.graphics.Bitmap

/**
 * Class for uploading images into and downloading images from database.
 */
interface ImageLoader {
    /**
     * Uploads image to database.
     *
     * @param imageBitmap image to upload
     * @param toUrl URL in Firebase Storage to upload
     * @param onSuccess is called, if operation is successful
     * @param onError is called, if operation is not successful
     */
    fun upload(
        imageBitmap: Bitmap,
        toUrl: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    /**
     * Downloads image from database, using standard Firebase API.
     *
     * @param fromUrl URL in Firebase Storage for downloading
     * @param onSuccess is called, if operation is successful
     * @param onError is called, if operation is not successful
     */
    fun download(
        fromUrl: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    )

    /**
     * Downloads image from database, using Glide.
     *
     * @param fromUrl URL in Firebase Storage for downloading
     * @param onSuccess is called, if operation is successful
     * @param onError is called, if operation is not successful
     */
    fun downloadWithGlide(
        fromUrl: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    )
}