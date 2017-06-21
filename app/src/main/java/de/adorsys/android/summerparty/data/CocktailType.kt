package de.adorsys.android.summerparty.data

import android.os.Parcel
import android.os.Parcelable

data class CocktailType(val name: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CocktailType> = object : Parcelable.Creator<CocktailType> {
            override fun createFromParcel(source: Parcel): CocktailType = CocktailType(source)
            override fun newArray(size: Int): Array<CocktailType?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
    }
}