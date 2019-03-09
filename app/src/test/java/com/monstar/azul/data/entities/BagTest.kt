package com.monstar.azul.data.entities

import org.junit.Test

import org.junit.Assert.*

class BagTest {

    private val bag by lazy {
        val tiles = arrayListOf(
            Tile(0, TileType.RED),
            Tile(1, TileType.BLACK),
            Tile(2, TileType.YELLOW)
        )
        Bag(tiles, mutableListOf())
    }

    @Test
    fun size() {
        assertEquals(3, bag.size())
    }

    @Test
    fun getFromBag() {
        val fromBag = bag.getFromBagAndRemove(1)

        assertEquals(1, fromBag.size)
        assertEquals(TileType.RED, fromBag[0].tileType)
        assertEquals(2, bag.size())

        assertEquals(TileType.BLACK, bag[0].tileType)
        assertEquals(TileType.YELLOW, bag[1].tileType)
    }
}