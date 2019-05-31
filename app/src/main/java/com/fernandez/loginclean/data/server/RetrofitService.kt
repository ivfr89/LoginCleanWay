package com.fernandez.loginclean.data.server

import com.fernandez.loginclean.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


//Use dependency injection or whatever you want, this is an example
object RetrofitService
{


    private val clientBuilder by lazy { OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HeaderInterceptor())
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS).build() }

    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.END_POINT_URL)
            .client(clientBuilder)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val serviceApp by lazy {
        retrofitBuilder.create(ApiService::class.java)
    }




}