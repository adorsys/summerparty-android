package de.adorsys.android.network

import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.network.mutable.MutableOrder
import retrofit2.Call
import retrofit2.http.*

interface CocktailService {
    @POST("order")
    fun createOrder(@Body mutableOrder: MutableOrder): Call<Void>

    @GET("order/{id}")
    fun getOrder(@Path("id") id: String): Call<Order>

    @GET("order")
    fun getOrders(@Query("state") state: String? = ""): Call<List<Order>>

    @GET("beverage")
    fun getCocktails(): Call<List<Cocktail>>

    @GET("beverage/{id}")
    fun getCocktail(@Path("id") id: String): Call<Cocktail>

    @POST("customer")
    fun createCustomer(@Body mutableCustomer: MutableCustomer): Call<Customer>

    @GET("customer/{id}")
    fun getCustomer(@Path("id") id: String): Call<Customer>

    @GET("customer/{id}/order")
    fun getOrdersForCustomer(@Path("id") id: String): Call<List<Order>>
}
