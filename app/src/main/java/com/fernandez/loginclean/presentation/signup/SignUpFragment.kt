package com.fernandez.loginclean.presentation.signup

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
import com.fernandez.loginclean.domain.uc.user.GoogleSignIn
import com.fernandez.loginclean.domain.uc.user.SignUp
import com.fernandez.loginclean.presentation.access.AccessInterface
import com.fernandez.loginclean.presentation.access.ProfileActivity
import com.fernandez.loginclean.presentation.login.LoginFragment
import com.fernandez.loginclean.utils.Constants
import com.fernandez.loginclean.utils.Constants.GOOGLE.GOOGLE_SIGN_CODE
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpFragment: Fragment()
{
    private lateinit var mViewModel: SignUpFragmentViewModel
    private val mSharedPreferencesManager: SharedPreferencesManager by lazy {
        SharedPreferencesManager.getInstance(context!!)
    }
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {

        fun newInstance(): SignUpFragment
        {
            return SignUpFragment().apply {
                val args = Bundle()
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_signup,container,false)
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

            googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(activity!!, gso)
        }
    }


    private fun initStates()
    {

        if(!::mViewModel.isInitialized)
            mViewModel = ViewModelProviders.of(this,
                SignUpFragmentViewModel.SignUpFragmentViewModelFactory(
                    SignUp(
                        UserRepository.Network(
                            NetworkHandler(context!!),
                            RetrofitService.serviceApp,
                            ServerResponseMapper
                        )
                    ),
                    GoogleSignIn(
                        UserRepository.Network(
                            NetworkHandler(context!!),
                            RetrofitService.serviceApp,
                            ServerResponseMapper
                        ),
                        mSharedPreferencesManager
                    )
                ))[SignUpFragmentViewModel::class.java]

        mViewModel.state.observe(::getLifecycle,::updateUI)
        mViewModel.failure.observe(::getLifecycle,::handleErrors)

    }





    private fun updateUI(screenState: ScreenState<SignUpScreenState>?) {

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

    private fun renderScreenState(screenState: SignUpScreenState)
    {
        when(screenState)
        {
            is SignUpScreenState.UserRegistered ->{
//                example of message


                notifyWithAction(coordinatorSignUp,R.string.signup_success,R.string.go)
                {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                    }
                    startActivity(Intent.createChooser(intent,getString(R.string.email_client)))
                }

            }
            is SignUpScreenState.UserGoogleSignUp ->{
                Toast.makeText(context,getString(R.string.username_success,screenState.user.firstname),Toast.LENGTH_LONG).show()

                context?.let {

                    startActivity(ProfileActivity.newIntent(it,screenState.user.accounts))
                }
            }
        }
    }

    private fun handleErrors(failure: Failure?) {

        hideButtons()

        when(failure)
        {
            is Failure.NullResult -> {
                Toast.makeText(context, getString(R.string.username_null), Toast.LENGTH_SHORT).show()
            }
            is Failure.NetworkConnection -> {
                Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            }

            is SignUp.FirstNameWrong -> {
                Toast.makeText(context, getString(R.string.firstname_wrong), Toast.LENGTH_SHORT).show()
                inpFirstName.error = getString(R.string.field_error)
            }
            is SignUp.LastNameWrong -> {
                Toast.makeText(context, getString(R.string.lastname_wrong), Toast.LENGTH_SHORT).show()
                inpLastName.error = getString(R.string.field_error)
            }
            is SignUp.EmailWrong -> {
                Toast.makeText(context, getString(R.string.email_wrong), Toast.LENGTH_SHORT).show()
                inpLastName.error = getString(R.string.field_error)
            }
            is SignUp.RequireFields -> {
                Toast.makeText(context, getString(R.string.require_fields_login), Toast.LENGTH_SHORT).show()
                inpFirstName.error = getString(R.string.require_fields_signup)
                inpLastName.error = getString(R.string.require_fields_signup)
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
                Toast.makeText(context, getString(R.string.signup_401_error), Toast.LENGTH_LONG).show()
            }
            400 -> {
                Toast.makeText(context, getString(R.string.signup_400_error), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun loadButtons()
    {
        btnSignUp.startAnimation()
    }

    private fun hideButtons()
    {
        btnSignUp.revertAnimation()
    }

    private fun configureUI()
    {
        btnSignUp.setOnClickListener {
            mViewModel.signUp(inpFirstName.text.toString(),inpLastName.text.toString(), inpEmail.text.toString())
        }

        txtSignIn.setOnClickListener {
            (activity as? AccessInterface)?.loadFragment(LoginFragment.newInstance())
        }

        btnSocialGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == Constants.GOOGLE.GOOGLE_SIGN_CODE) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
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