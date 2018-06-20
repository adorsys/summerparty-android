package de.adorsys.android.summerparty.data

import android.content.Context
import android.graphics.drawable.Drawable
import de.adorsys.android.summerparty.R

object CocktailUtils {
    fun getCocktailDrawableForName(context: Context, id: String?): Drawable? {
        when (id) {
            CocktailType.PINA_COLADA.nomination
            -> return context.resources.getDrawable(R.drawable.pina_colada, context.theme)
            CocktailType.SEX_ON_THE_BEACH.nomination
            -> return context.resources.getDrawable(R.drawable.sex_on_the_beach, context.theme)
            CocktailType.CAIPIRINHA.nomination
            -> return context.resources.getDrawable(R.drawable.caipirinha, context.theme)
            CocktailType.MAI_TAI.nomination
            -> return context.resources.getDrawable(R.drawable.mai_tai, context.theme)
            CocktailType.COCONUT_KISS.nomination
            -> return context.resources.getDrawable(R.drawable.coconut_kiss, context.theme)
            CocktailType.SUNFLOWER.nomination
            -> return context.resources.getDrawable(R.drawable.sunflower, context.theme)
            CocktailType.WODKA_LEMON.nomination
            -> return context.resources.getDrawable(R.drawable.wodka_lemon, context.theme)
            CocktailType.CUBA_LIBRE.nomination
            -> return context.resources.getDrawable(R.drawable.cubalibre, context.theme)
            CocktailType.APEROL_SPRITZ.nomination
            -> return context.resources.getDrawable(R.drawable.aperol_sprizz, context.theme)
            CocktailType.GIN_TONIC.nomination
            -> return context.resources.getDrawable(R.drawable.gin_tonic, context.theme)
            CocktailType.VODKA_BULL.nomination
            -> return context.resources.getDrawable(R.drawable.wodka_bull, context.theme)
            CocktailType.MOJITO.nomination
            -> return context.resources.getDrawable(R.drawable.mojito, context.theme)
            CocktailType.PINK_GIN_TONIC.nomination
            -> return context.resources.getDrawable(R.drawable.pink_tonic, context.theme)




        }
        return context.resources.getDrawable(R.drawable.cocktail_ordered, context.theme)
    }

    fun cocktailListToMap(cocktails: List<Cocktail>): HashMap<Cocktail, Int> {
        val cocktailMap = HashMap<Cocktail, Int>()
        for (cocktail in cocktails) {
            if (cocktailMap.containsKey(cocktail)) {
                var idCount = cocktailMap[cocktail]
                idCount = idCount?.plus(1)
                idCount.let {
                    cocktailMap.remove(cocktail)
                    cocktailMap.put(cocktail, it!!)
                }
            } else {
                cocktailMap[cocktail] = 1
            }
        }
        return cocktailMap
    }

    fun cocktailMapToIdList(cocktailMap: Map<Cocktail, Int>): List<String> {
        val cocktailIds = ArrayList<String>()
        for ((key, count) in cocktailMap) {
            var counter = 0
            while (counter < count) {
                cocktailIds.add(key.id)
                counter += 1
            }
        }
        return cocktailIds
    }
}
