package de.adorsys.android.summerparty.data

import android.os.Parcel
import android.os.Parcelable

class Cocktail(val id: String, val available: Boolean, val type: CocktailType?) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Cocktail> = object : Parcelable.Creator<Cocktail> {
            override fun createFromParcel(source: Parcel): Cocktail = Cocktail(source)
            override fun newArray(size: Int): Array<Cocktail?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    1 == source.readInt(),
    source.readParcelable<CocktailType>(CocktailType::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeInt((if (available) 1 else 0))
        dest.writeParcelable(type, 0)
    }
}