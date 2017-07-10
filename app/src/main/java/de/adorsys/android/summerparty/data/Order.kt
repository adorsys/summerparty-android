package de.adorsys.android.summerparty.data

import android.os.Parcel
import android.os.Parcelable

data class Order(private val id: String, val state: String, val beverages: List<Cocktail>, private val customer: Customer) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Order> = object : Parcelable.Creator<Order> {
            override fun createFromParcel(source: Parcel): Order = Order(source)
            override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.createTypedArrayList(Cocktail.CREATOR),
    source.readParcelable<Customer>(Customer::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(state)
        dest.writeTypedList(beverages)
        dest.writeParcelable(customer, 0)
    }
}