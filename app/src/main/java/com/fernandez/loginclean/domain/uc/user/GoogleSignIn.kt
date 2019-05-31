package com.fernandez.loginclean.domain.uc.user

import android.accounts.Account
import com.fernandez.loginclean.core.App
import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import com.fernandez.loginclean.utils.Constants
import com.google.android.gms.auth.GoogleAuthUtil
import kotlinx.coroutines.CoroutineScope

class GoogleSignIn(val userRepository: UserRepository,
                   val preferencesManager: SharedPreferencesManager
): UseCase<User, GoogleSignIn.Params, CoroutineScope>(){



    override suspend fun run(params: Params): Either<Failure, User> {

        val scopes = "oauth2:profile email"
        val token = GoogleAuthUtil.getToken(App.globalContext, params.account, scopes)


        val result = userRepository.signinSocial(params.social,token)

        return when(result)
        {
            is Either.Left ->{
                result
            }
            is Either.Right ->{

                if(result.b!=null)
                {
                    preferencesManager.setUserData(result.b)
                    Either.Right(result.b)
                }
                else
                    Either.Left(Failure.NullResult())
            }
        }

    }


    class Params(val social: String= Constants.GOOGLE.SOCIAL_LOGIN, val account: Account?)



}

