package com.fernandez.loginclean.domain.models

import android.os.Parcel
import android.os.Parcelable
import com.fernandez.loginclean.core.extensions.empty
import com.fernandez.loginclean.data.server.EntityProfile
import com.fernandez.loginclean.data.server.EntityUser
import com.google.gson.Gson

data class User(val id: Int,
                val firstname: String,
                val lastname: String,
                val email: String,
                val secret: String,
                val accounts: List<Profile>

) : Parcelable {

    companion object {
        fun empty() = User(0, "", String.empty(), String.empty(),String.empty(), listOf())

        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }

        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: String.empty(),
        parcel.readString() ?: String.empty(),
        parcel.readString() ?: String.empty(),
        parcel.readString() ?: String.empty(),
        parcel.createTypedArrayList(Profile.CREATOR) ?: listOf()
    )

    fun isEmpty(): Boolean = this == empty()

    fun toEntity() = EntityUser(id,firstname,lastname,email,secret,accounts.toList())
    fun toJson() = Gson().toJson(this)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstname)
        parcel.writeString(lastname)
        parcel.writeString(email)
        parcel.writeString(secret)
        parcel.writeTypedList(accounts)
    }

    override fun describeContents(): Int {
        return 0
    }

}


data class Profile(val id: Int,
                   val name: String,
                   val image: String?) : Parcelable {
    companion object {
        fun empty() = Profile(0,String.empty(), String.empty())

        @JvmField
        val CREATOR: Parcelable.Creator<Profile> = object : Parcelable.Creator<Profile> {
            override fun createFromParcel(parcel: Parcel): Profile {
                return Profile(parcel)
            }

            override fun newArray(size: Int): Array<Profile?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: String.empty(),
        parcel.readString()

    )
    fun isEmpty(): Boolean = this == empty()

    fun toEntity() = EntityProfile(id,name,image)

    fun toJson() = Gson().toJson(this)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }


}


data class ValueString(val name: String)
