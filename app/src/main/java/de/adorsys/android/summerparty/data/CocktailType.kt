package de.adorsys.android.summerparty.data

enum class CocktailType(val id: Int) {
    ITALIAN_COLADA(1),
    SEX_ON_THE_BEACH(2),
    CAIPIRINHA(3),
    MAI_TAI(4),
    CHINATOWN(5),
    COCONUT_KISS(6),
    SUNFLOWER(7),
    WODKA_LEMON(8),
    CUBA_LIBRE(9),
    APEROL_SPRITZ(10),
    GIN_TONIC(11);

    companion object {
        fun cocktailForId(id: Int): CocktailType? {
            return values().firstOrNull { it.id == id }
        }
    }
}