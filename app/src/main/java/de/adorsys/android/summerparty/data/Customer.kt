package de.adorsys.android.summerparty.data

import android.os.Parcel
import android.os.Parcelable

// TODO check if fields are correct when specified
class Customer(val id: String, val name: String, val pushId: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Customer> = object : Parcelable.Creator<Customer> {
            override fun createFromParcel(source: Parcel): Customer = Customer(source)
            override fun newArray(size: Int): Array<Customer?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(pushId)
    }
}