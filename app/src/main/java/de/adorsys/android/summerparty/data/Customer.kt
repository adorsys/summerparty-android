package de.adorsys.android.summerparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customer(val id: String, val name: String, val pushId: String) : Parcelable