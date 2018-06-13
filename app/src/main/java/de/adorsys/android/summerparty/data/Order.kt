package de.adorsys.android.summerparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(private val id: String, val state: String, val beverages: List<Cocktail>, private val customer: Customer) : Parcelable