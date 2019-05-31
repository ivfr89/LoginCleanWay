package com.fernandez.loginclean.data.server

import android.util.Log
import com.fernandez.loginclean.core.App
import com.fernandez.loginclean.domain.models.SharedPreferencesManager
import com.fernandez.loginclean.utils.Constants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.IOException


interface ApiService {


    companion object Factory {

        const val USERS_ENDPOINT = "users/"


        const val POST_SIGNUP = "signup"
        const val POST_SIGNIN = "signin"

        const val GET_SIGNIN = "signin"
        const val GET_SESSION = "session"
        const val GET_RECOVERY_PASSWORD = "recoveryPassword/{email}"

    }

    @retrofit2.http.GET(USERS_ENDPOINT+GET_SESSION) fun getSession(): Call<String>
    @retrofit2.http.GET(USERS_ENDPOINT+GET_RECOVERY_PASSWORD) fun getRecoveryPassword(@Query("email") email: String): Call<Unit>

    @retrofit2.http.POST(USERS_ENDPOINT+POST_SIGNUP) fun postSignUp(@Body body: String): Call<String>

    @FormUrlEncoded
    @retrofit2.http.POST(USERS_ENDPOINT+POST_SIGNIN) fun postSignin(@FieldMap body: Map<String,String>): Call<String>




}


//I had use a header interceptor por easy implementation, you can use whatever you want

class HeaderInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var requestBuilder: Request.Builder
        val sharedPreferencesManager by lazy {
            SharedPreferencesManager.getInstance(App.globalContext)
        }
        val user = sharedPreferencesManager.getUserData()


        var sessionCode: String? = user?.secret
        var sessionUser: Int? = user?.id



        requestBuilder = request.newBuilder()
            .addHeader("Secret", Constants.CREDENTIALS.SECRET)
            .addHeader("Token", Constants.CREDENTIALS.TOKEN)
            .addHeader("API-apptype", "android")
            .addHeader("Content-Type", "application/json")

        if (sessionUser != null && sessionCode != null)
            requestBuilder.addHeader("SESSION-GI", "${sessionUser}.${sessionCode.toString()}")


//UNCOMMENT TO SEE WHAT ARE WE SENDING
        Log.i("REQUEST",
            String.format("Sending request %s on %s %s", request.url(), chain.connection(), request.headers()))

        val response = chain.proceed(requestBuilder.build())

//UNCOMMENT TO SEE WHAT ARE WE RECEIVING
        Log.i("REQUEST",
            String.format("Received response for %s, headers: %s", request.url(), response.body()))

        val body = ResponseBody.create(response.body()?.contentType(), response.body()!!.string())
        return response.newBuilder().body(body).build()
    }
}

