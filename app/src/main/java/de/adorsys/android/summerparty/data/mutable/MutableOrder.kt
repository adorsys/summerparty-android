package de.adorsys.android.summerparty.data.mutable

import de.adorsys.android.summerparty.data.MutableCustomer

data class MutableOrder(val cocktailIds: List<String>, val customer: MutableCustomer)
