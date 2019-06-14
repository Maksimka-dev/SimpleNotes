package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.entity.VisibleNote

/**
 * Model, that is responsible for operations in MainActivity.
 */
interface NoteDetailsModel {
    /**
     * Saves note to database.
     */
    fun saveNote(
        visibleNote: VisibleNote,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    /**
     * Loads existing note from database.
     */
    fun downloadNote(
        noteId: String,
        onSuccess: (VisibleNote) -> Unit,
        onError: (Throwable) -> Unit
    )

    /**
     * Provides id for new note.
     */
    fun newNoteId(): String

    /**
     * Deletes existing note from database.
     */
    fun deleteNote(
        noteId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

}