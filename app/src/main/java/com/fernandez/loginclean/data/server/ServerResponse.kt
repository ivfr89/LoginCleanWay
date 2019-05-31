package com.fernandez.loginclean.data.server

import com.fernandez.loginclean.core.ModelEntity
import com.fernandez.loginclean.core.extensions.fromJson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import org.json.JSONArray


object ServerResponseMapper
{

    inline fun <reified T: ModelEntity> parseObjectResponse(jsonString: String): T?
    {
        var objectResponse : T? = null
        try {

            objectResponse = jsonString.fromJson()

        } catch (e: JsonParseException) {
        }

        return objectResponse



    }

    inline fun <reified T:ModelEntity> parseArrayResponse(jsonString: String): List<T>
    {

        val genericListType : MutableList<T> = mutableListOf()


        try {
            val json = JSONArray(jsonString)


            for (i in 0 until json.length()) {
                val messageString = json[i].toString()

                try {

                    genericListType += messageString.fromJson<T>()

                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }

        } catch (e: JsonParseException) {
        }

        return genericListType
    }




}