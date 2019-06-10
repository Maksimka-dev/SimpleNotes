package com.epam.training.simplenotes.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URL

class DefaultImageLoader(private val storage: FirebaseStorage) : ImageLoader {

    override fun upload(
        imageBitmap: Bitmap,
        toUrl: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val storageReference = storage.reference
        val fileReference = storageReference.child(toUrl)
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
}