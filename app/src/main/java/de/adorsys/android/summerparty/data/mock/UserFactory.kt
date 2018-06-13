package de.adorsys.android.summerparty.data.mock

import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import java.util.*

object UserFactory {
    fun create(): MutableCustomer = MutableCustomer("Heiner" + Random().nextInt())
}
