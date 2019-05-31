package com.fernandez.loginclean.presentation.login

import android.accounts.Account
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.loginclean.core.BaseViewModel
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.uc.user.RecoverPassword
import com.fernandez.loginclean.domain.uc.user.SignIn
import com.fernandez.loginclean.domain.uc.user.GoogleSignIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LoginFragmentViewModel(val signIn: SignIn,
                             val recoverPassword: RecoverPassword,
                             val googleSignin: GoogleSignIn
): BaseViewModel()
{
    private val _state: MutableLiveData<ScreenState<LoginScreenState>> = MutableLiveData()

    val state: LiveData<ScreenState<LoginScreenState>>
        get()= _state

    private var job = Job()

    private var coroutineScope = CoroutineScope(Dispatchers.IO + job)



    fun resetStates()
    {
        this._state.value = null
        this._failure.value = null
    }

    fun login(name: String, password: String)
    {
        this._state.value = ScreenState.Loading

        signIn.execute({it.either(::handleFailure, ::handleUser)},
            SignIn.Params(name,password),coroutineScope)
    }

    fun googleLogin(account: Account?)
    {
        this._state.value = ScreenState.Loading

        googleSignin.execute({it.either(::handleFailure, ::handleUser)},
            GoogleSignIn.Params(account=account),coroutineScope)

    }
    fun recoverPassword(email: String)
    {
        this._state.value = ScreenState.Loading

        recoverPassword.execute({it.either(::handleFailure, ::handleRecoverPassword)},
            RecoverPassword.Params(email),coroutineScope)
    }

    private fun handleRecoverPassword(unit: Unit) {

        this._state.value = ScreenState.Render(LoginScreenState.PasswordRecovered())

    }


    private fun handleUser(user: User)
    {
        this._state.value = ScreenState.Render(LoginScreenState.ShowUser(user))
    }


    override fun onCleared() {
        super.onCleared()

        job.cancel()
    }


    class LoginFragmentViewModelFactory(val signIn: SignIn,
                                        val recoverPassword: RecoverPassword,
                                        val socialSignIn: GoogleSignIn

    ) : ViewModelProvider.NewInstanceFactory()
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginFragmentViewModel(signIn,recoverPassword,socialSignIn) as T
        }
    }



}