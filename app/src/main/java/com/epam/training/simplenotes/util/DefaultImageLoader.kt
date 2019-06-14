package com.epam.training.simplenotes.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.epam.training.simplenotes.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URL

class DefaultImageLoader(
    private val storage: FirebaseStorage,
    private val context: Context
) : ImageLoader {

    override fun upload(
        imageBitmap: Bitmap,
        toUrl: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val storageReference = storage.reference
        val fileReference = storageReference.child(toUrl)
        //удалять картинку, которая уже там лежала - сначала, потом сохранять по тому же урлу

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val uploadTask = fileReference.putBytes(data)

        uploadTask.addOnCompleteListener { task ->
            val taskResult = task.result
            if (task.isSuccessful && taskResult != null) {
                onSuccess()
            } else {
                onError(InternalError())
            }
        }.addOnFailureListener {
            onError(it)
        }
    }

    override fun download(
        fromUrl: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        storage.reference.child(fromUrl).downloadUrl.addOnSuccessListener { uri ->
            execute(
                {
                    lateinit var icon: Bitmap

                    try {
                        val connection = URL(uri.toString()).openStream()
                        icon = BitmapFactory.decodeStream(connection)
                        Log.d("Download result", "${icon == null}")
                    } catch (e: Exception) {
                        Log.e("Download error", e.message)
                        e.printStackTrace()
                    }

                    icon
                },
                {
                    onSuccess(it)
                },
                {
                    onError(it)
                    Log.e("Download error", it.message)
                }
            )
        }.addOnFailureListener {
            onError(it)
            Log.e("Download error", it.message)
        }

    }

    override fun downloadWithGlide(
        fromUrl: String,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val imageReference = storage.getReference(fromUrl)

        GlideApp.with(context)
            .asBitmap()
            .load(imageReference)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onSuccess(resource)
                    Log.d("Download GLIDE result", "Bitmap is loaded")
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                    onError(InternalError("Can't load image with Glide"))
                    Log.d("Download GLIDE result", "onLoadCleared() called")
                }
            })
    }
}