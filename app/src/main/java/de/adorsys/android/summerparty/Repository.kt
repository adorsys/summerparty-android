package de.adorsys.android.summerparty

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import de.adorsys.android.network.ApiManager
import de.adorsys.android.network.Cocktail
import de.adorsys.android.network.Customer
import de.adorsys.android.network.Order
import de.adorsys.android.network.mutable.MutableCustomer
import de.adorsys.android.network.mutable.MutableOrder
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

object Repository {
    private var _user: Customer? = null
    private val _cocktailList = mutableListOf<Cocktail>()
    private val _pendingCocktailList = mutableListOf<Cocktail>()
    private val _orderList = mutableListOf<Order>()

    private val _cocktailsLiveData = MutableLiveData<List<Cocktail>>()
    private val _pendingCocktailsLiveData = MutableLiveData<List<Cocktail>>()
    private val _ordersLiveData = MutableLiveData<List<Order>>()
    private val _userLiveData = MutableLiveData<Customer>()

    val user: Customer?
        get() = _user

    val cocktailList: List<Cocktail>
        get() = _cocktailList

    val pendingCocktailList: List<Cocktail>
        get() = _pendingCocktailList

    val orderList: List<Order>
        get() = _orderList

    val cocktailsLiveData: LiveData<List<Cocktail>>
        get() = _cocktailsLiveData

    val userLiveData: LiveData<Customer>
        get() = _userLiveData

    val pendingCocktailsLiveData: LiveData<List<Cocktail>>
        get() = _pendingCocktailsLiveData

    val ordersLiveData: LiveData<List<Order>>
        get() = _ordersLiveData

    fun addToPendingCocktails(cocktail: Cocktail) {
        _pendingCocktailList.add(cocktail)
        _pendingCocktailsLiveData.postValue(_pendingCocktailList)
    }

    fun removeFromPendingCocktails(cocktail: Cocktail) {
        val toRemove = mutableListOf<Cocktail>()
        _pendingCocktailList.forEach {
            if (it.type == cocktail.type) {
                toRemove.add(it)
            }
        }
        _pendingCocktailList.removeAll(toRemove)
        _pendingCocktailsLiveData.postValue(_pendingCocktailList)
    }

    fun fetchStartupData(userId: String): Deferred<Boolean> {
        return async {
            try {
                // get cocktails
                val cocktailResponse = ApiManager.getCocktails().await()
                if (cocktailResponse.isSuccessful) {
                    _cocktailList.clear()
                    _cocktailList.addAll(cocktailResponse.body().orEmpty())
                    _cocktailsLiveData.postValue(_cocktailList)
                } else {
                    return@async false
                }

                // get orders
                val orderResponse = ApiManager.getOrdersForCustomer(userId).await()
                if (orderResponse.isSuccessful) {
                    _orderList.clear()
                    _orderList.addAll(orderResponse.body().orEmpty())
                    _ordersLiveData.postValue(_orderList)
                } else {
                    return@async false
                }
                true
            } catch (e: Exception) {
                Log.e(javaClass.name, e.message)
                false
            }
        }
    }

    fun createUser(customer: MutableCustomer, onReadyAction: (Customer?) -> Unit) {
        launch {
            try {
                val response = ApiManager.createCustomer(customer).await()
                if (response.isSuccessful) {
                    _user = response.body()
                    onReadyAction(_user)
                } else {
                    onReadyAction(null)
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, e.message)
                onReadyAction(null)
            }
        }
    }

    fun getUser(id: String, onReadyAction: ((Customer?) -> Unit)? = null, forceReload: Boolean = false) {
        if (_user != null && !forceReload) {
            onReadyAction?.let { it(_user) }
            _userLiveData.postValue(_user)
            return
        }

        launch {
            try {
                val response = ApiManager.getCustomer(id).await()
                if (response.isSuccessful) {
                    _user = response.body()
                    onReadyAction?.let { it(_user) }
                    _userLiveData.postValue(_user)
                } else {
                    onReadyAction?.let { it(null) }
                    _userLiveData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, e.message)
                onReadyAction?.let { it(null) }
                _userLiveData.postValue(null)
            }
        }
    }

    fun fetchCocktails(onReadyAction: ((List<Cocktail>) -> Unit)? = null, forceReload: Boolean = false) {
        if (_cocktailList.isNotEmpty() || !forceReload) {
            onReadyAction?.let { it(_cocktailList) }
            _cocktailsLiveData.postValue(_cocktailList)
            return
        }

        launch {
            try {
                val response = ApiManager.getCocktails().await()
                if (response.isSuccessful) {
                    _cocktailList.clear()
                    _cocktailList.addAll(response.body().orEmpty())
                    onReadyAction?.let { it(_cocktailList) }
                    _cocktailsLiveData.postValue(_cocktailList)
                } else {
                    Log.e("TAG_COCKTAILS", response.message())
                    onReadyAction?.let { it(emptyList()) }
                }
            } catch (e: Exception) {
                Log.e("TAG_COCKTAILS", e.message)
                onReadyAction?.let { it(emptyList()) }
            }
        }
    }

    fun fetchOrders(onReadyAction: ((List<Order>) -> Unit)? = null, forceReload: Boolean = false) {
        if (_orderList.isNotEmpty() || !forceReload) {
            onReadyAction?.let { it(_orderList) }
            _ordersLiveData.postValue(_orderList)
            return
        }

        launch {
            if (user == null) {
                Log.e("TAG_CUSTOMER_ORDERS", "user is null")
                onReadyAction?.let { it(emptyList()) }
                return@launch
            }

            try {
                val response = ApiManager.getOrdersForCustomer(user!!.id).await()
                if (response.isSuccessful) {
                    _orderList.clear()
                    _orderList.addAll(response.body().orEmpty())
                    onReadyAction?.let { it(_orderList) }
                    _ordersLiveData.postValue(_orderList)
                } else {
                    Log.e("TAG_CUSTOMER_ORDERS", response.message())
                    onReadyAction?.let { it(emptyList()) }
                }
            } catch (e: Exception) {
                onReadyAction?.let { it(emptyList()) }
                Log.e("TAG_CUSTOMER_ORDERS", e.message)
            }
        }
    }

    fun createOrder(currentOrder: MutableOrder, onReadyAction: (Boolean) -> Unit) {
        launch(UI) {
            try {
                val response = ApiManager.createOrder(currentOrder).await()
                if (response.isSuccessful) {
                    onReadyAction(true)
                    _pendingCocktailList.clear()
                    _pendingCocktailsLiveData.postValue(_pendingCocktailList)
                    fetchOrders(forceReload = true)
                    Log.d("TAG_ORDER_CREATE", "create order successful")
                } else {
                    onReadyAction(false)
                    Log.d("TAG_ORDER_CREATE", "create order not successful")
                }
            } catch (e: Exception) {
                Log.e("TAG_ORDER_CREATE", e.message)
                onReadyAction(false)
            }
        }
    }
}