package com.monstar.azul.data.entities

import com.monstar.azul.domain.util.getAllAndRemove
import com.monstar.azul.domain.util.getFirstItemsAndRemove

sealed class BagException(message: String) : Throwable(message) {
    class TooLowTilesInBagException(count: Int) : BagException("Bag has only $count tiles")
}

data class Bag(val tiles: MutableList<Tile>, val drop: MutableList<Tile>) {

    fun size() = tiles.size

    operator fun get(i: Int): Tile {
        return tiles[i]
    }

    fun getFromBagAndRemove(count: Int): List<Tile> {
        if (count > size()) {
            throw BagException.TooLowTilesInBagException(size())
        }

        return tiles.getFirstItemsAndRemove(count)
    }

    fun shuffle() {
        tiles.shuffle()
    }

    fun shuffleWithDrop() {
        tiles.addAll(drop.getAllAndRemove())
    }
}