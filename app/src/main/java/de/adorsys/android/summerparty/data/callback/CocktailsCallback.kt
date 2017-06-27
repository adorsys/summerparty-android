package de.adorsys.android.summerparty.data.callback

import de.adorsys.android.summerparty.data.Cocktail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CocktailsCallback : Callback<List<Cocktail>> {
    override fun onResponse(call: Call<List<Cocktail>>, response: Response<List<Cocktail>>) {

    }

    override fun onFailure(call: Call<List<Cocktail>>, t: Throwable) {

    }
}
