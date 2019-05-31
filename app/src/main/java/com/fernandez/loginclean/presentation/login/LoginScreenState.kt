package com.fernandez.loginclean.presentation.login

import com.fernandez.loginclean.domain.models.User

sealed class LoginScreenState
{
    class ShowUser(val user: User): LoginScreenState()
    class PasswordRecovered: LoginScreenState()
}