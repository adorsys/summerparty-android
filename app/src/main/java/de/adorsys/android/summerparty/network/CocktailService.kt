package de.adorsys.android.summerparty.network

import de.adorsys.android.summerparty.data.Cocktail
import de.adorsys.android.summerparty.data.Order
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CocktailService {
    @POST("order")
    fun createOrder(@Body mutableOrder: MutableOrder): Call<Void>

    @GET("order/{id}")
    fun getOrder(@Path("id") id: String): Call<Order>

    @GET("beverage")
    fun getCocktails(): Call<List<Cocktail>>

    @GET("beverage/{id}")
    fun getCocktail(@Path("id") id: String): Call<Cocktail>
}
