package com.fernandez.loginclean.domain.repository

import com.fernandez.loginclean.core.BaseRepository
import com.fernandez.loginclean.core.Failure
import com.fernandez.loginclean.core.extensions.empty
import com.fernandez.loginclean.data.server.ApiService
import com.fernandez.loginclean.data.server.EntityUser
import com.fernandez.loginclean.data.server.NetworkHandler
import com.fernandez.loginclean.data.server.ServerResponseMapper
import com.fernandez.loginclean.domain.models.User
import com.fernandez.loginclean.domain.uc.Either
import org.json.JSONObject

interface UserRepository
{

    fun signup(firstname: String, lastname: String, email: String): Either<Failure,Unit>
    fun signin(email: String, password: String): Either<Failure,User?>
    fun signinSocial(social: String, token: String): Either<Failure,User?>

    fun session(): Either<Failure,User?>
    fun recoveryPassword(email: String): Either<Failure,Unit>



    class Network
    constructor(val networkHandler: NetworkHandler,
                val service: ApiService,
                val serverMapper: ServerResponseMapper
    ) : BaseRepository(), UserRepository {


        override fun signup(firstname: String, lastname: String, email: String): Either<Failure,Unit> {

            val body = JSONObject()

            body.put("firstname",firstname)
            body.put("lastname",lastname)
            body.put("email",email)

            return if(networkHandler.isConnected)
            {
                request(service.postSignUp(body.toString()),{ response->
                },String.empty())
            }else
                Either.Left(Failure.NetworkConnection())

        }

        override fun signinSocial(social: String, token: String): Either<Failure, User?> {

            val body = mapOf("social" to social,
                "token" to token)

            return if(networkHandler.isConnected)
            {
                request(service.postSignin(body),{ response->

                    serverMapper.parseObjectResponse<EntityUser>(response)?.toDomain()

                },String.empty())
            }else
                Either.Left(Failure.NetworkConnection())

        }


        override fun signin(email: String, password: String): Either<Failure,User?> {

            val body = mapOf("email" to email,
                                             "password" to password)

            return if(networkHandler.isConnected)
            {
                request(service.postSignin(body),{response->
                    serverMapper.parseObjectResponse<EntityUser>(response)?.toDomain()
//                    response.toDomain()
                },String.empty())
            }else{
                Either.Left(Failure.NetworkConnection())
            }

        }

        override fun session(): Either<Failure,User?> {

            return if(networkHandler.isConnected)
            {
                request(service.getSession(),{response->
                    serverMapper.parseObjectResponse<EntityUser>(response)?.toDomain()
//                    response.toDomain()
                },String.empty())
            }else{
                Either.Left(Failure.NetworkConnection())
            }


        }

        override fun recoveryPassword(email: String): Either<Failure,Unit> {

            return if(networkHandler.isConnected) {
                request(service.getRecoveryPassword(email), { unit ->

                }, Unit)
            }else
                Either.Left(Failure.NetworkConnection())
        }


    }


}