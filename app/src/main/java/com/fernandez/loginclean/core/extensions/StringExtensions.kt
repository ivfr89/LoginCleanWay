package com.fernandez.loginclean.core.extensions

import com.google.gson.Gson
import java.lang.StringBuilder

fun String.Companion.empty() = ""


inline fun <reified T> String.toModel(): T = Gson().fromJson(this,T::class.java)


fun Map<String,String>.composeJson(): String
{
    val stringJson = StringBuilder()
    var index = 0

    if(!this.isEmpty())
    {
        stringJson.append("{")


        for((key,value) in this)
        {
            stringJson.append("\""+key+"\"")
            stringJson.append(":")
            stringJson.append("\""+value+"\"")

            if(index!=this@composeJson.size-1)
                stringJson.append(",")

            index++
        }
        stringJson.append("}")


    }

    return stringJson.toString()
}