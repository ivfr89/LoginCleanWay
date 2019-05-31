package com.fernandez.loginclean.domain.uc.user

import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import kotlinx.coroutines.CoroutineScope





class RecoverPassword(val userRepository: UserRepository): UseCase<Unit, RecoverPassword.Params, CoroutineScope>(){

    class EmailWrong: Failure.FeatureFailure()
    class RequiredField: Failure.FeatureFailure()



    override suspend fun run(params: Params): Either<Failure, Unit> {

        val validator = Validator.validate(params.email)

        when (validator)
        {
            is Either.Left ->
                return validator.left(validator.a)
            is Either.Right ->{
                return userRepository.recoveryPassword(params.email)
            }
        }


    }


    object Validator
    {
        fun validate(email: String): Either<Failure, Unit>
        {
            return if(!email.trim().contains("@"))
                Either.Left(EmailWrong())
            else if(email.trim().isEmpty())
                Either.Left(RequiredField())
            else
                Either.Right(Unit)
        }
    }


    class Params(val email: String)


}