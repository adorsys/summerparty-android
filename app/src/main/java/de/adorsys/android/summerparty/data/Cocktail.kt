package de.adorsys.android.summerparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cocktail(val id: String, val available: Boolean, val name: String?, val type: String?): Parcelable