package com.epam.training.simplenotes.model

interface LoginModel {
    fun isUserAuthenticated(): Boolean
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )

    fun registerAccount(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )

}