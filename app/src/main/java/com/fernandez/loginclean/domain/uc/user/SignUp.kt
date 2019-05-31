package com.fernandez.loginclean.domain.uc.user

import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import kotlinx.coroutines.CoroutineScope


class SignUp(val userRepository: UserRepository): UseCase<Unit, SignUp.Params, CoroutineScope>(){

    class FirstNameWrong: Failure.FeatureFailure()
    class LastNameWrong: Failure.FeatureFailure()
    class EmailWrong: Failure.FeatureFailure()
    class RequireFields: Failure.FeatureFailure()



    override suspend fun run(params: Params): Either<Failure, Unit> {

        val validator = Validator.validate(
            params.firstname,
            params.lastname,
            params.email
        )

        when (validator)
        {
            is Either.Left ->
                return validator.left(validator.a)
            is Either.Right ->{

                return userRepository.signup(params.firstname,params.lastname,params.email)
            }
        }


    }


    object Validator
    {
        fun validate(firstname: String, lastname: String, email: String): Either<Failure, Unit>
        {
            return if(!email.trim().contains("@"))
                Either.Left(EmailWrong())
            else if(firstname.trim().length<3)
                Either.Left(FirstNameWrong())
            else if(lastname.trim().length<3)
                Either.Left(LastNameWrong())
            else if(firstname.trim().isEmpty()
                || lastname.trim().isEmpty()
                || email.trim().isEmpty())
                Either.Left(RequireFields())
            else
                Either.Right(Unit)
        }
    }


    class Params(val firstname: String,val lastname: String, val email: String)


}