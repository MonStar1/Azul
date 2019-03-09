package com.monstar.azul.domain

import com.monstar.azul.data.entities.TileType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateGameTest {

    val game = CreateGame().createGame(listOf(mockk(), mockk()))

    @Test
    fun createBag() {
        val bag = game.bag

        assertEquals(100, bag.tiles.size)

        assertEquals(20, bag.tiles.filter { it.tileType == TileType.RED }.size)
        assertEquals(20, bag.tiles.filter { it.tileType == TileType.BLACK }.size)
        assertEquals(20, bag.tiles.filter { it.tileType == TileType.BLUE }.size)
        assertEquals(20, bag.tiles.filter { it.tileType == TileType.YELLOW }.size)
        assertEquals(20, bag.tiles.filter { it.tileType == TileType.GREEN }.size)
    }

    @Test
    fun createCircles() {
        val circles = game.circles

        assertEquals(5, circles.size)
    }
}