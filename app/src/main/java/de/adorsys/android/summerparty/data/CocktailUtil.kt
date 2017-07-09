package de.adorsys.android.summerparty.data

import android.content.Context
import android.graphics.drawable.Drawable
import de.adorsys.android.summerparty.R

class CocktailUtil {
    companion object {
        fun getCocktailDrawableForId(context: Context, id: String): Drawable? {
            when (id.toInt()) {
                CocktailType.ITALIAN_COLADA.id
                -> return context.resources.getDrawable(R.drawable.pina_colada, context.theme)
                CocktailType.SEX_ON_THE_BEACH.id
                -> return context.resources.getDrawable(R.drawable.sex_on_the_beach, context.theme)
                CocktailType.CAIPIRINHA.id
                -> return context.resources.getDrawable(R.drawable.caipirinha, context.theme)
                CocktailType.MAI_TAI.id
                -> return context.resources.getDrawable(R.drawable.mai_tai, context.theme)
                CocktailType.CHINATOWN.id
                -> return context.resources.getDrawable(R.drawable.chinatown, context.theme)
                CocktailType.COCONUT_KISS.id
                -> return context.resources.getDrawable(R.drawable.coconut_kiss, context.theme)
                CocktailType.SUNFLOWER.id
                -> return context.resources.getDrawable(R.drawable.sunflower, context.theme)
                CocktailType.WODKA_LEMON.id
                -> return context.resources.getDrawable(R.drawable.wodka_lemon, context.theme)
                CocktailType.CUBA_LIBRE.id
                -> return context.resources.getDrawable(R.drawable.cuba_libre, context.theme)
                CocktailType.APEROL_SPRITZ.id
                -> return context.resources.getDrawable(R.drawable.aperol_sprizz, context.theme)
                CocktailType.GIN_TONIC.id
                -> return context.resources.getDrawable(R.drawable.gin_tonic, context.theme)
            }
            return null
        }
    }
}
