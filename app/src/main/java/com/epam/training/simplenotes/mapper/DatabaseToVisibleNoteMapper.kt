package com.epam.training.simplenotes.mapper

import com.epam.training.simplenotes.entity.DatabaseNote
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.util.ImageLoader

class DatabaseToVisibleNoteMapper(private val loader: ImageLoader) : Mapper<DatabaseNote, VisibleNote> {

    override fun map(
        noteFrom: DatabaseNote,
        userId: String,
        onSuccess: (VisibleNote) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val visibleNote = VisibleNote(
            id = noteFrom.id,
            title = noteFrom.title,
            text = noteFrom.text
        )

        noteFrom.imageUrl?.let {
            loader.downloadWithGlide(
                it,
                { bitmap ->
                    visibleNote.imageBitmap = bitmap
                    onSuccess(visibleNote)
                },
                { exception ->
                    visibleNote.imageBitmap = null
                    onError(exception)
                    onSuccess(visibleNote)
                }
            )
        } ?: onSuccess(visibleNote)
    }
}