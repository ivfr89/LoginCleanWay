package com.fernandez.loginclean.presentation.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernandez.loginclean.core.BaseViewModel
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.uc.user.UserAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class AccessActivityViewModel(private val userAccess: UserAccess): BaseViewModel()
{

    private val _state: MutableLiveData<ScreenState<AccessScreenState>> = MutableLiveData()

    val state: LiveData<ScreenState<AccessScreenState>>
        get()= _state

    val job = Job()

    val coroutineScope = CoroutineScope(Dispatchers.Default + job)


    fun session()
    {
        _state.value = ScreenState.Loading

        userAccess.execute({it.either(::handleFailure,::handleSession)},Unit,coroutineScope)
    }

    private fun handleSession(user: User) {

        _state.value = ScreenState.Render(AccessScreenState.ShowUser(user))

    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    class AccessActivityViewModelFactory(val userAccess: UserAccess) : ViewModelProvider.NewInstanceFactory()
    {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AccessActivityViewModel(userAccess) as T
        }
    }
}