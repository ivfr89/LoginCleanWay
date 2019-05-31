package com.fernandez.loginclean.presentation.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fernandez.loginclean.R
import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.core.ScreenState
import com.fernandez.loginclean.core.extensions.notifyWithAction
import com.fernandez.loginclean.data.server.NetworkHandler
import com.fernandez.loginclean.data.server.RetrofitService
import com.fernandez.loginclean.data.server.ServerResponseMapper
import com.fernandez.loginclean.data.server.gso
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.user.RecoverPassword
import com.fernandez.loginclean.domain.uc.user.SignIn
import com.fernandez.loginclean.presentation.access.AccessInterface
import com.fernandez.loginclean.presentation.access.ProfileActivity
import com.fernandez.loginclean.presentation.signup.SignUpFragment
import com.fernandez.loginclean.utils.Constants
import com.fernandez.loginclean.utils.Constants.GOOGLE.GOOGLE_SIGN_CODE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment: Fragment(), GoogleApiClient.OnConnectionFailedListener {


    private val mSharedPreferencesManager: SharedPreferencesManager by lazy {
        SharedPreferencesManager.getInstance(context!!)
    }

    private lateinit var mViewModel: LoginFragmentViewModel

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient



    companion object {

        fun newInstance(): LoginFragment
        {
            return LoginFragment().apply {
                val args = Bundle()
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_login,container,false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStates()
        configureUI()
        configureSocialLogin()


    }

    private fun configureSocialLogin()
    {
        context?.let {
            FirebaseApp.initializeApp(it)

            mFirebaseAuth = FirebaseAuth.getInstance()

            googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        }
    }


    private fun initStates()
    {
        if(!::mViewModel.isInitialized)
            mViewModel = ViewModelProviders.of(this,
                LoginFragmentViewModel.LoginFragmentViewModelFactory(
                    SignIn(
                        UserRepository.Network(
                            NetworkHandler(context!!),
                            RetrofitService.serviceApp, ServerResponseMapper
                        ),
                        mSharedPreferencesManager
                    ),
                    RecoverPassword(
                        UserRepository.Network(
                            NetworkHandler(context!!),
                            RetrofitService.serviceApp, ServerResponseMapper
                        )
                    ),
                    com.fernandez.loginclean.domain.uc.user.GoogleSignIn(
                        UserRepository.Network(
                            NetworkHandler(context!!),
                            RetrofitService.serviceApp, ServerResponseMapper
                        ),
                        mSharedPreferencesManager
                    )
                ))[LoginFragmentViewModel::class.java]

        mViewModel.resetStates()
        mViewModel.state.observe(::getLifecycle,::updateUI)
        mViewModel.failure.observe(::getLifecycle,::handleErrors)

    }




    private fun updateUI(screenState: ScreenState<LoginScreenState>?) {

        when(screenState)
        {
            is ScreenState.Loading->{
                loadButtons()
            }
            is ScreenState.Render ->{
                hideButtons()

                renderScreenState(screenState.renderState)

            }
        }

    }

    private fun renderScreenState(screenState: LoginScreenState)
    {
        when(screenState)
        {
            is LoginScreenState.ShowUser ->{
//                example of message
                Toast.makeText(context,getString(R.string.username_success,screenState.user.email),Toast.LENGTH_LONG).show()

                context?.let {

                    startActivity(ProfileActivity.newIntent(it,screenState.user.accounts))
                }

            }
            is LoginScreenState.PasswordRecovered -> {

                notifyWithAction(coordinatorLogin,R.string.recovery_password_success,R.string.go)
                {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                    }
                    startActivity(Intent.createChooser(intent,getString(R.string.email_client)))
                }
            }
        }
    }

    private fun handleErrors(failure: Failure?) {

        hideButtons()



        when(failure)
        {
            is Failure.NullResult -> {
                Toast.makeText(context, getString(R.string.username_null),Toast.LENGTH_SHORT).show()
            }
            is Failure.NetworkConnection -> {
                Toast.makeText(context, getString(R.string.no_connection),Toast.LENGTH_SHORT).show()
            }

            is SignIn.UserNameWrong -> {
                Toast.makeText(context, getString(R.string.username_wrong),Toast.LENGTH_SHORT).show()
                inpUsername.error = getString(R.string.field_error)
            }
            is SignIn.PasswordWrong -> {
                Toast.makeText(context, getString(R.string.password_wrong),Toast.LENGTH_SHORT).show()
                inpPassword.error = getString(R.string.field_error)
            }
            is SignIn.RequireFields -> {
                Toast.makeText(context, getString(R.string.require_fields_login),Toast.LENGTH_SHORT).show()
                inpUsername.error = getString(R.string.require_fields_login)
                inpPassword.error = getString(R.string.require_fields_login)
            }
            is RecoverPassword.RequiredField ->{
                Toast.makeText(context, getString(R.string.require_fields_recover_password),Toast.LENGTH_SHORT).show()
                inpUsername.error = getString(R.string.field_error)
            }
            is RecoverPassword.EmailWrong ->{
                Toast.makeText(context, getString(R.string.email_wrong),Toast.LENGTH_SHORT).show()
                inpUsername.error = getString(R.string.field_error)
            }

            is Failure.ServerErrorCode ->{

                handleErrorCodes(failure)

            }
        }

    }

    private fun handleErrorCodes(errorCode: Failure.ServerErrorCode)
    {
        when(errorCode.code)
        {
            401 -> {
                Toast.makeText(context, getString(R.string.login_401_error),Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadButtons()
    {
        btnEnter.startAnimation()
    }

    private fun hideButtons()
    {
        btnEnter.revertAnimation()
    }

    private fun configureUI()
    {
        btnEnter.setOnClickListener {
            mViewModel.login(inpUsername.text.toString(),inpPassword.text.toString())
        }
        txtForgotpassword.setOnClickListener {
            mViewModel.recoverPassword(inpUsername.text.toString())
        }
        txtSignUp.setOnClickListener {
            (activity as? AccessInterface)?.loadFragment(SignUpFragment.newInstance())
        }

        btnSocialGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_CODE)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == Constants.GOOGLE.GOOGLE_SIGN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                signInSocial(account!!)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }

    }

    private fun signInSocial(acct: GoogleSignInAccount) {

        context?.let {

            mViewModel.googleLogin(account = acct.account)
        }

    }


}