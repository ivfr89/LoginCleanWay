package com.fernandez.loginclean.domain.uc.user

import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.repository.UserRepository
import com.fernandez.loginclean.domain.uc.Either
import com.fernandez.loginclean.domain.uc.UseCase
import kotlinx.coroutines.CoroutineScope

class UserAccess(val userRepository: UserRepository,
             val preferencesManager: SharedPreferencesManager
): UseCase<User, Unit, CoroutineScope>(){


    override suspend fun run(params: Unit): Either<Failure, User> {


            val result = userRepository.session()

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
