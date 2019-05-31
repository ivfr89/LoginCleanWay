package com.fernandez.loginclean.domain.uc.user

import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CoroutineScope

class Logout(val sharedPreferencesManager: SharedPreferencesManager,
             val googleSignClient: GoogleSignInClient
             ): UseCase<Unit, Unit, CoroutineScope>(){


    override suspend fun run(params: Unit): Either<Failure, Unit> {

        sharedPreferencesManager.setUserData(null)
        googleSignClient.signOut()

        return Either.Right(Unit)

    }


}