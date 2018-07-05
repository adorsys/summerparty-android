package de.adorsys.android.summerpartysocial

import java.util.Date

data class Post(val id: String = "",
                val name: String = "",
                val image: String = "",
                val text: String = "",
                val timestamp: Date = Date())