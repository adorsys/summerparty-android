package de.adorsys.android.summerparty.data

import de.adorsys.android.summerparty.data.mutable.MutableCustomer
import de.adorsys.android.summerparty.data.mutable.MutableOrder
import de.adorsys.android.summerparty.network.CocktailService
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class ApiManager {
    INSTANCE;

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://summerparty.adorsys.de/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val cocktailService: CocktailService = retrofit.create(CocktailService::class.java)

    fun createOrder(mutableOrder: MutableOrder, orderCallback: Callback<Void>) {
        return cocktailService.createOrder(mutableOrder).enqueue(orderCallback)
    }

    fun getOrder(id: String, orderCallback: Callback<Order>) {
        return cocktailService.getOrder(id).enqueue(orderCallback)
    }

    fun getCocktails(cocktailsCallback: Callback<List<Cocktail>>) {
        return cocktailService.getCocktails().enqueue(cocktailsCallback)
    }

    fun getCocktail(id: String, cocktailCallback: Callback<Cocktail>) {
        return cocktailService.getCocktail(id).enqueue(cocktailCallback)
    }

    fun createCustomer(mutableCustomer: MutableCustomer, customerCallback: Callback<Customer>) {
        return cocktailService.createCustomer(mutableCustomer).enqueue(customerCallback)
    }

    fun getCustomer(id: String, customerCallback: Callback<Customer>) {
        return cocktailService.getCustomer(id).enqueue(customerCallback)
    }

    fun getOrdersForCustomer(id: String, ordersCallback: Callback<List<Order>>) {
        return cocktailService.getOrdersForCustomer(id).enqueue(ordersCallback)
    }
}
