package com.epam.training.simplenotes.mapper

import com.epam.training.simplenotes.entity.DatabaseNote
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.util.ImageLoader

class VisibleToDatabaseNoteMapper(private val loader: ImageLoader) : Mapper<VisibleNote, DatabaseNote> {

    override fun map(
        noteFrom: VisibleNote,
        userId: String, onSuccess: (DatabaseNote) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val databaseNote = DatabaseNote(
            id = noteFrom.id,
            title = noteFrom.title,
            text = noteFrom.text
        )

        noteFrom.imageBitmap?.let {
            val toUrl = "$userId/${noteFrom.id}/IMG_${System.currentTimeMillis()}"
//            val toUrl = "$userId/${noteFrom.id}/IMG_${noteFrom.id}"

            loader.upload(
                it,
                toUrl,
                {
                    databaseNote.imageUrl = toUrl
                    onSuccess(databaseNote)
                },
                { exception ->
                    onError(exception)
                }
            )
        } ?: onSuccess(databaseNote)
    }
}