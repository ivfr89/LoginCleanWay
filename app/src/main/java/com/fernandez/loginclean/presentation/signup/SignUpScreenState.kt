package com.fernandez.loginclean.presentation.signup

import com.fernandez.loginclean.domain.models.User

sealed class SignUpScreenState
{
    class UserRegistered(): SignUpScreenState()
    class UserGoogleSignUp(val user: User): SignUpScreenState()
}