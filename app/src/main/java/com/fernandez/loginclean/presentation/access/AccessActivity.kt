package com.fernandez.loginclean.presentation.access

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fernandez.loginclean.R
import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.core.extensions.loadFragment
import com.fernandez.loginclean.data.server.NetworkHandler
import com.fernandez.loginclean.data.server.RetrofitService
import com.fernandez.loginclean.data.server.ServerResponseMapper
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.user.UserAccess
import com.fernandez.loginclean.presentation.loader.LoaderFragment
import com.fernandez.loginclean.presentation.login.LoginFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

/*
* @author  Iván Fernández Rico, Globalincubator
*/
class AccessActivity : AppCompatActivity(), AccessInterface, GoogleApiClient.OnConnectionFailedListener {

    //Its Lazy, put the logic inside a UC if you want


    private var stateSaved=false

    private val mSharedPreferencesManager: SharedPreferencesManager by lazy {
        SharedPreferencesManager.getInstance(this)
    }



    private lateinit var mViewModel: AccessActivityViewModel




    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)

        stateSaved=savedInstanceState!=null

        initStates()

        //Get my session

        mViewModel.session()

    }



    private fun initStates()
    {
        if(!::mViewModel.isInitialized)
            mViewModel = ViewModelProviders.of(this,
                AccessActivityViewModel.AccessActivityViewModelFactory(
                    UserAccess(
                        UserRepository.Network(
                            NetworkHandler(this),
                            RetrofitService.serviceApp, ServerResponseMapper
                        ),
                        mSharedPreferencesManager
                    )
                ))[AccessActivityViewModel::class.java]

        mViewModel.state.observe(::getLifecycle,::updateUI)
        mViewModel.failure.observe(::getLifecycle,::handleErrors)

    }

    private fun handleErrors(failure: Failure?) {


        mSharedPreferencesManager.setUserData(null)
        hideLoaderError()
        when(failure)
        {
            is Failure.NullResult -> {
                Toast.makeText(this, getString(R.string.username_null), Toast.LENGTH_SHORT).show()
            }
            is Failure.NetworkConnection -> {
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun updateUI(screenState: ScreenState<AccessScreenState>?) {

        when(screenState)
        {
            is ScreenState.Loading ->{
                showLoader()
            }
            is ScreenState.Render -> {

                renderScreenState(screenState.renderState)


            }
        }

    }

    private fun renderScreenState(renderState: AccessScreenState) {

        when(renderState)
        {
            is AccessScreenState.ShowUser ->{
                if(mSharedPreferencesManager.getUserData()!=null)
                {
                    startActivity(ProfileActivity.newIntent(this,mSharedPreferencesManager.getUserData()!!.accounts))
                }else
                {
                    if(!stateSaved)
                        loadFragment(LoginFragment.newInstance())

                }
            }
        }

    }

    private fun showLoader()
    {
        loadFragment(LoaderFragment.newInstance())
    }

    private fun hideLoaderError()
    {
        loadFragment(LoginFragment.newInstance())
    }


    override fun loadFragment(fragment: Fragment) {
        supportFragmentManager.loadFragment {
            setCustomAnimations(R.anim.slide_left_in,
                R.anim.slide_left_out).replace(R.id.frmMain, fragment)
        }
    }

}
