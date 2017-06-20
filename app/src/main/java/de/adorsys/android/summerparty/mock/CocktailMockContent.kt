package de.adorsys.android.summerparty.mock

import de.adorsys.android.summerparty.CocktailItem
import java.util.*

/**
 * Helper class for providing sample content for user interfaces
 */
object CocktailMockContent {

	/**
	 * An array of mock items.
	 */
	val ITEMS: MutableList<CocktailItem> = ArrayList()

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	val ITEM_MAP: MutableMap<String, CocktailItem> = HashMap()

	private val COUNT = 25

	init {
		// Add some sample items.
		for (i in 1..COUNT) {
			addItem(createDummyItem(i))
		}
	}

	private fun addItem(item: CocktailItem) {
		ITEMS.add(item)
		ITEM_MAP.put(item.id, item)
	}

	private fun createDummyItem(position: Int): CocktailItem {
		return CocktailItem(position.toString(), "", "Cocktail Item " + position, makeDetails(position))
	}

	private fun makeDetails(position: Int): String {
		val builder = StringBuilder()
		builder.append("Details about Item: ").append(position)
		for (i in 0..position - 1) {
			builder.append("\nMore details information here.")
		}
		return builder.toString()
	}
}
