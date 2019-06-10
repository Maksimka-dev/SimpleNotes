package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.entity.VisibleNote

interface NoteDetailsModel {
    fun saveNote(
        visibleNote: VisibleNote,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun downloadNote(
        noteId: String,
        onSuccess: (VisibleNote) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun newNoteId(): String

    fun deleteNote(
        noteId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

}