package com.fernandez.loginclean.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.loginclean.core.BaseViewModel
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.domain.uc.user.Logout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainActivityViewModel(val logout: Logout): BaseViewModel()
{
    private val _state: MutableLiveData<ScreenState<MainScreenState>> = MutableLiveData()

    val state: LiveData<ScreenState<MainScreenState>>
        get()= _state

    private var job = Job()

    private var coroutineScope = CoroutineScope(Dispatchers.Default + job)



    fun logout()
    {
        this._state.value = ScreenState.Loading

        logout.execute({it.either(::handleFailure, ::handleLogout)},Unit ,coroutineScope)
    }

    private fun handleLogout(nothing: Unit) {

        _state.value = ScreenState.Render(
            MainScreenState.UserLogout()
        )
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    class MainActivityViewModelFactory(val signUp: Logout) : ViewModelProvider.NewInstanceFactory()
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(signUp) as T
        }
    }



}