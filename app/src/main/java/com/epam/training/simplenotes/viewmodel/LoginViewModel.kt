package com.epam.training.simplenotes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.epam.training.simplenotes.model.LoginModel

/**
 * ViewModel for LoginActivity and its content.
 */
class LoginViewModel(
    private val loginModel: LoginModel
) : ViewModel() {

    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean>
        get() = _authState

    /**
     * Calls to model to check, is user already authenticated or not.
     */
    fun isUserAuthenticated(): Boolean {

        return loginModel.isUserAuthenticated()
    }

    /**
     * Calls to model to try to sing in to the existing Firebase account with given credentials.
     */
    fun signIn(email: String, password: String) {
        loginModel.signIn(
            email,
            password,
            {
                _authState.postValue(true)
            },
            {
                _authState.postValue(false)
            })
    }

    /**
     * Calls to model to try to create new Firebase account, using given e-mail and password,
     * and to sign in created account.
     */
    fun registerAccount(email: String, password: String) {
        loginModel.registerAccount(
            email,
            password,
            {
                _authState.postValue(true)
            },
            {
                _authState.postValue(false)
            }
        )
    }

}