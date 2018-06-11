package de.adorsys.android.summerparty.data

import android.util.Log
import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.network.CocktailService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://summerparty.adorsys.de/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val cocktailService: CocktailService = retrofit.create(CocktailService::class.java)

    fun createOrder(mutableOrder: MutableOrder): Deferred<Response<Void>> {
        return async(CommonPool) { cocktailService.createOrder(mutableOrder).execute() }
    }

    fun getCocktails(): Deferred<Response<List<Cocktail>>> {
        return async(CommonPool) { cocktailService.getCocktails().execute() }
    }

    fun createCustomer(mutableCustomer: MutableCustomer): Deferred<Response<Customer>> {
        return async(CommonPool) { cocktailService.createCustomer(mutableCustomer).execute() }
    }

    fun getCustomer(id: String): Deferred<Response<Customer>> {
        Log.i("TAG_CUSTOMER", id)
        return async(CommonPool) { cocktailService.getCustomer(id).execute() }
    }

    fun getOrdersForCustomer(id: String): Deferred<Response<List<Order>>> {
        Log.i("TAG_CUSTOMER", id)
        return async(CommonPool) { cocktailService.getOrdersForCustomer(id).execute() }
    }
}
