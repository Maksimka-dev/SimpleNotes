package com.epam.training.simplenotes.model

/**
 * Model, that is responsible for login operations in app.
 */
interface LoginModel {
    /**
     * Checks, is user already authenticated or not.
     */
    fun isUserAuthenticated(): Boolean

    /**
     * Tries to sing in to the existing Firebase account with given credentials.
     * @param onSuccess will be called, if operation is successful.
     * @param onError will be called, if some error emerges during signing in.
     */
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )

    /**
     * Tries to create new Firebase account, using given e-mail and password, and to sign in created account.
     * @param onSuccess will be called, if operation is successful.
     * @param onError will be called, if some error emerges during signing in.
     */
    fun registerAccount(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )
}