package de.adorsys.android.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customer(val id: String, val name: String, val pushId: String) : Parcelable