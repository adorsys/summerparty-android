package de.adorsys.android.network

import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.network.mutable.MutableOrder
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiManager {
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://summerparty.adorsys.de/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    private val cocktailService: CocktailService = retrofit.create(CocktailService::class.java)

    fun createOrder(mutableOrder: MutableOrder): Deferred<Response<Void>> {
        return async { cocktailService.createOrder(mutableOrder).execute() }
    }

    fun getOrders(state: String?): Deferred<Response<List<Order>>> {
        return async { cocktailService.getOrders(state).execute() }
    }

    fun getCocktails(): Deferred<Response<List<Cocktail>>> {
        return async { cocktailService.getCocktails().execute() }
    }

    fun createCustomer(mutableCustomer: MutableCustomer): Deferred<Response<Customer>> {
        return async { cocktailService.createCustomer(mutableCustomer).execute() }
    }

    fun getCustomer(id: String): Deferred<Response<Customer>> {
        return async { cocktailService.getCustomer(id).execute() }
    }

    fun getOrdersForCustomer(id: String): Deferred<Response<List<Order>>> {
        return async { cocktailService.getOrdersForCustomer(id).execute() }
    }
}
