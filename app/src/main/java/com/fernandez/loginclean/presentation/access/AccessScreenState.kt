package com.fernandez.loginclean.presentation.access

import com.fernandez.loginclean.domain.models.User

sealed class AccessScreenState
{
    class ShowUser(val user: User): AccessScreenState()
}