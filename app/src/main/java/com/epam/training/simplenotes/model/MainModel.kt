package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.entity.VisibleNote

/**
 * Model, that is responsible for operations in MainActivity.
 */
interface MainModel {

    /**
     * Gets a list of all saved user notes and returns it via onSuccess() call.
     */
    fun loadUserNotes(
        onSuccess: (MutableList<VisibleNote>) -> Unit,
        onError: (Throwable) -> Unit
    )

    /**
     * Signs out from application.
     */
    fun signOut(onComplete: () -> Unit)
}