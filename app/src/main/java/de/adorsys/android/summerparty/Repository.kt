package de.adorsys.android.summerparty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.adorsys.android.network.ApiManager
import de.adorsys.android.network.Customer
import de.adorsys.android.network.mutable.MutableCustomer
import kotlinx.coroutines.experimental.launch

object Repository {
    private var _user: Customer? = null
    private val _userLiveData = MutableLiveData<Customer>()

    val user: Customer?
        get() = _user

    val userLiveData: LiveData<Customer>
        get() = _userLiveData

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
}