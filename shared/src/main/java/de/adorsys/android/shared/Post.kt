package de.adorsys.android.shared

import java.util.*

data class Post(val id: String? = null,
                val name: String? = "",
                val image: String? = "",
                val imageReference: String? = "",
                val text: String? = "",
                val timestamp: Date? = Date())