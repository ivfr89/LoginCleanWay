package com.fernandez.loginclean.domain.uc.user

import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import kotlinx.coroutines.CoroutineScope



class SignIn(val userRepository: UserRepository,
             val preferencesManager: SharedPreferencesManager
             ): UseCase<User, SignIn.Params, CoroutineScope>(){

    class UserNameWrong: Failure.FeatureFailure()
    class PasswordWrong: Failure.FeatureFailure()
    class RequireFields: Failure.FeatureFailure()
    class NoError: Failure.FeatureFailure()


    override suspend fun run(params: Params): Either<Failure, User> {

        val validator =
            Validator.validate(params.email, params.password)

        when (validator)
        {
            is Either.Left ->
                return validator.left(validator.a)
            is Either.Right ->{

                val result = userRepository.signin(params.email,params.password)

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
        }

    }


    class Params(val email: String, val password: String)


    object Validator
    {
        fun validate(username: String, password: String): Either<Failure, Unit>
        {
            return if(!username.trim().contains("@"))
                Either.Left(UserNameWrong())
            else if(password.trim().length<5)
                Either.Left(PasswordWrong())
            else if(username.trim().isEmpty() || password.trim().isEmpty())
                Either.Left(RequireFields())
            else
                Either.Right(Unit)
        }
    }


}



