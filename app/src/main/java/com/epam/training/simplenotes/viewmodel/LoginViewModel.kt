package com.epam.training.simplenotes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.epam.training.simplenotes.model.LoginModel

class LoginViewModel(
    private val loginModel: LoginModel
) : ViewModel() {

    private val _authState = MutableLiveData<Boolean>()
    val authState: LiveData<Boolean>
        get() = _authState

    //проверяет, есть ли уже аутентифицированные почта и пароль
    fun isUserAuthenticated(): Boolean {

        return loginModel.isUserAuthenticated()
    }

    //входит в аккаунт в firebase
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

    override fun onCleared() {
        Log.d("LoginViewModel", "onCleared() called!")
        super.onCleared()
    }
}