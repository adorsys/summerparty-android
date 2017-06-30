package de.adorsys.android.summerparty.data.mock

import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import java.util.*

class UserFactory {
    companion object Factory {
        fun create(): MutableCustomer = MutableCustomer("Heiner" + Random().nextInt())
    }
}
