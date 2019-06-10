package com.epam.training.simplenotes.mapper

interface Mapper<F, T> {
    fun map(
        noteFrom: F,
        userId: String,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    )
}
