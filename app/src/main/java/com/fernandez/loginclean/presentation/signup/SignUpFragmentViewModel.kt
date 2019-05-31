package com.fernandez.loginclean.presentation.signup

import android.accounts.Account
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.loginclean.core.BaseViewModel
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.uc.user.GoogleSignIn
import com.fernandez.loginclean.domain.uc.user.SignUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SignUpFragmentViewModel(val signUp: SignUp,
                              val googleSignIn: GoogleSignIn): BaseViewModel()
{
    private val _state: MutableLiveData<ScreenState<SignUpScreenState>> = MutableLiveData()

    val state: LiveData<ScreenState<SignUpScreenState>>
        get()= _state

    private var job = Job()

    private var coroutineScope = CoroutineScope(Dispatchers.IO + job)



    fun signUp(firstName: String, lastName: String, email: String)
    {
        this._state.value = ScreenState.Loading

        signUp.execute({it.either(::handleFailure, ::handleUser)}, SignUp.Params(firstName,lastName,email),coroutineScope)
    }

    fun googleLogin(account: Account?)
    {
        this._state.value = ScreenState.Loading

        googleSignIn.execute({it.either(::handleFailure, ::handleUserSignIn)},
            GoogleSignIn.Params(account=account),coroutineScope)

    }

    private fun handleUserSignIn(user: User) {

        _state.value = ScreenState.Render(
            SignUpScreenState.UserGoogleSignUp(user)
        )


    }

    private fun handleUser(nothing: Unit) {

        _state.value = ScreenState.Render(
            SignUpScreenState.UserRegistered()
        )
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    class SignUpFragmentViewModelFactory(val signUp: SignUp,
                                         val googleSignIn: GoogleSignIn) : ViewModelProvider.NewInstanceFactory()
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SignUpFragmentViewModel(signUp,googleSignIn) as T
        }
    }



}