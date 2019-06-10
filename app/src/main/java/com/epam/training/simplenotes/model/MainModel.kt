package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.entity.VisibleNote

interface MainModel {
    fun loadUserNotes(
        onSuccess: (MutableList<VisibleNote>) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun signOut(onComplete: () -> Unit)
}