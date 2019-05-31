package com.fernandez.loginclean.data.server

import com.fernandez.loginclean.core.ModelEntity
import com.fernandez.loginclean.core.extensions.empty
import com.fernandez.loginclean.core.extensions.fromJson
import com.fernandez.loginclean.domain.models.Profile
import com.fernandez.loginclean.domain.models.User

data class EntityUser(val id: Int,
                      val firstname: String,
                      val lastname: String,
                      val email: String,
                      val secret: String,
                      val accounts: List<Profile>

): ModelEntity(){


    override fun toModel(json: String): ModelEntity = json.fromJson()

    companion object {
        fun empty() = EntityUser(0, String.empty(), String.empty(), String.empty(),String.empty(), listOf())
    }

    fun isEmpty(): Boolean = this == empty()

    fun toDomain() = User(id,firstname,lastname,email,secret,accounts.toList())

}


data class EntityProfile(val id: Int,
                         val name: String,
                         val image: String?
                         ): ModelEntity()
{
    override fun toModel(json: String): ModelEntity = json.fromJson()

    companion object {
        fun empty() = EntityProfile(0, String.empty(), String.empty())
    }

    fun isEmpty(): Boolean = this == empty()

    fun toDomain() = Profile(id,name,image)


}