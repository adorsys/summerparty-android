package de.adorsys.android.summerpartysocial

import com.google.firebase.Timestamp

data class Post(val id: String = "",
                val username: String = "",
                val image: String = "",
                val text: String = "",
                val timestamp: Timestamp = Timestamp(0, 0))