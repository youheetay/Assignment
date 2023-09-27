package com.example.assignment
import android.os.Parcel
import android.os.Parcelable

data class Food(
    var id: String? = null,
    var foodName: String? = null,
    var foodDes: String? = null,
    var userId: String? = null,
    var image: String?= null,
    var quantity: Int ?= null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(foodName)
        parcel.writeString(foodDes)
        parcel.writeString(userId)
        parcel.writeString(image)
        parcel.writeInt(quantity ?: 0) // Use a default value (e.g., 0) if quantity is null
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }
}
