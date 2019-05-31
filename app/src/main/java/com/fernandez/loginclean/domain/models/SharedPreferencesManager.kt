package com.fernandez.loginclean.domain.models

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.fernandez.loginclean.core.extensions.toModel

class SharedPreferencesManager(val context: Context)
{

    private val PREF_NAME = "preferences"

    private val sharedPreferences by lazy {context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)}
    private val editor by lazy { sharedPreferences.edit() }


    private val KEY_USER = "user_serial"


    companion object {

//        Required an applicationContext
        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPreferencesManager?=null

        fun getInstance(context: Context): SharedPreferencesManager
        {
            synchronized(SharedPreferencesManager::class.java)
            {
                if(instance==null)
                    instance = SharedPreferencesManager(context)

                return instance!!
            }
        }
    }

    fun setUserData(user: User?){

        val json = user?.toJson()
        editor.putString(KEY_USER, json).apply()
    }

    fun getUserData(): User? = sharedPreferences.getString(KEY_USER,null)?.toModel()

}