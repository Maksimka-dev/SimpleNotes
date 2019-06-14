package com.epam.training.simplenotes.mapper

/**
 * Mapper for DatabaseNote and VisibleNote.
 */
interface Mapper<F, T> {
    /**
     * Allows to map object of type [F] to object of type [T].
     * Returns result via calling onSuccess().
     */
    fun map(
        noteFrom: F,
        userId: String,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    )
}
