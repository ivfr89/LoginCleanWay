package com.fernandez.loginclean.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application()
{

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var globalContext: Context

    }

    override fun onCreate() {
        super.onCreate()
        globalContext=applicationContext

    }
}