package com.example.assignment

import android.os.Parcel
import android.os.Parcelable


data class FoodR(var foodNameR: String ?= null,
                 var foodDesR: String ?= null,
                 var quantity : Int ?= null,
                 var userId: String ?= null,
                 var image: String?= null,
                 var id: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(foodNameR)
        parcel.writeString(foodDesR)
        parcel.writeString(userId)
        parcel.writeString(image)
        parcel.writeInt(quantity ?: 0) // Use a default value (e.g., 0) if quantity is null
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodR> {
        override fun createFromParcel(parcel: Parcel): FoodR {
            return FoodR(parcel)
        }

        override fun newArray(size: Int): Array<FoodR?> {
            return arrayOfNulls(size)
        }
    }
}