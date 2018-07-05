package de.adorsys.android.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(val id: String, val state: String, val beverages: List<Cocktail>, val customer: Customer) : Parcelable