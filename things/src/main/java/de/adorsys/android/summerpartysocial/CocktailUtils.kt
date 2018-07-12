package de.adorsys.android.summerpartysocial

import de.adorsys.android.network.Cocktail

object CocktailUtils {
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
