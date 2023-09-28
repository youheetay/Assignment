package com.example.assignment

import android.os.Parcel
import android.os.Parcelable

data class User(
                var userName : String ?= null,
                var gender : String ?= null,
                var DOB : String ?= null,
                var address : String ?= null,
                var image: String?= null,
                var userId : String ?= null)  : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(gender)
        parcel.writeString(DOB)
        parcel.writeString(address)
        parcel.writeString(image)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
