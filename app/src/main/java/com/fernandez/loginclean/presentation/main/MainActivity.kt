package com.fernandez.loginclean.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.fernandez.loginclean.R
import com.fernandez.loginclean.core.App
import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.core.extensions.loadImage
import com.fernandez.loginclean.data.server.gso
import com.fernandez.loginclean.domain.models.Profile
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.uc.user.Logout
import com.fernandez.loginclean.presentation.access.AccessActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_main.*

/*
* @author  Iván Fernández Rico, Globalincubator
*/

class MainActivity : AppCompatActivity() {

    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {

        const val ARG_PROFILE = "argProfile"

        fun newInstance(context: Context,profile: Profile): Intent
        {
            return Intent(context, MainActivity::class.java).apply {
                val bundle = Bundle()
                bundle.putParcelable(ARG_PROFILE,profile)
                putExtras(bundle)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureSocialLogin()

        initStates(googleSignInClient)





        val profile = intent.getParcelableExtra(ARG_PROFILE) as? Profile
        profile?.let {
            configureUI(it)
        }




    }

    private fun initStates(googleSignInClient: GoogleSignInClient)
    {
        if(!::mViewModel.isInitialized)
            mViewModel = ViewModelProviders.of(this,
                MainActivityViewModel.MainActivityViewModelFactory(
                    Logout(
                        SharedPreferencesManager.getInstance(App.globalContext),
                        googleSignInClient
                    )
                ))[MainActivityViewModel::class.java]


        mViewModel.state.observe(::getLifecycle,::updateUI)
        mViewModel.failure.observe(::getLifecycle,::handleErrors)
    }

    private fun handleErrors(failure: Failure?) {

//        Handle the errors

    }

    private fun updateUI(screenState: ScreenState<MainScreenState>?) {

        when(screenState)
        {
            is ScreenState.Loading ->{
                showProgress()
            }

            is ScreenState.Render -> {
                hideProgress()
                renderScreenStates(screenState.renderState)
            }

        }

    }

    private fun renderScreenStates(renderState: MainScreenState) {

        when(renderState)
        {
            is MainScreenState.UserLogout -> {
                startActivity(
                    Intent(this, AccessActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            else ->{

            }
        }


    }


    private fun showProgress()
    {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress()
    {
        progressBar.visibility = View.GONE
    }



    private fun configureSocialLogin()
    {
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun configureUI(profile: Profile)
    {
        imgUser.loadImage(profile.image)
        txtUserName.text = profile.name

        btnLogout.setOnClickListener {
            mViewModel.logout()
        }
    }



}
