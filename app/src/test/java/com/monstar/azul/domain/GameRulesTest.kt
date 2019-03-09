package com.monstar.azul.domain

import com.monstar.azul.data.entities.Player
import com.monstar.azul.data.entities.Point
import com.monstar.azul.data.entities.Tile
import com.monstar.azul.data.entities.TileType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class GameRulesTest {

    val createGame = CreateGame()
    val player1: Player = createGame.createPlayer(mockk())
    val game = createGame.createGame(listOf(player1))
    val gameRules = GameRules()

    @Test
    fun fillAllCircles() {
        gameRules.fillAllCircles(game)

        game.circles.forEach {
            assertEquals(4, it.tiles.size)
        }
    }

    @Test
    fun getTilesFromTableAndPlaceToLine() {
        gameRules.fillAllCircles(game)

        val tileType = game.circles[0].tiles[0].tileType

        gameRules.getTilesFromCircleAndPlaceToLine(
            game.table,
            player1,
            game.circles[0],
            tileType,
            player1.patternLines.lines[0]
        )

        assertEquals("Circle:", 0, game.circles[0].tiles.size)
        assertEquals("Line: ", 4 - player1.patternLines.lines[0].tiles.size, game.table.tiles.size)
    }

    @Test
    fun getTilesFromCircleAndPlaceToLine() {
        gameRules.fillAllCircles(game)

        game.table.tiles.addAll(listOf(Tile(0, TileType.GREEN), Tile(1, TileType.GREEN), Tile(2, TileType.GREEN)))

        gameRules.getTilesFromTableAndPlaceToLine(
            game,
            player1,
            TileType.GREEN,
            player1.patternLines.lines[0]
        )

        assertEquals("Table: ", 0, game.table.tiles.size)
        assertEquals("Line: ", 1, player1.patternLines.lines[0].tiles.size)
        assertEquals("Floor line (+1 first tile)", 2 + 1, player1.floorLine.tiles.size)
    }

    @Test
    fun calculateScore() {
        val tilesMap = player1.wall.tilesMap

        tilesMap.apply {
            put(Point(0, 0), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(1, gameRules.calculateScore(player1, Point(0, 0)))
        assertEquals(1, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(0, 1), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(2, gameRules.calculateScore(player1, Point(0, 1)))
        assertEquals(3, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(0, 2), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(3, gameRules.calculateScore(player1, Point(0, 2)))
        assertEquals(6, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(1, 0), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(2, gameRules.calculateScore(player1, Point(1, 0)))
        assertEquals(8, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(1, 1), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(4, gameRules.calculateScore(player1, Point(1, 1)))
        assertEquals(12, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(2, 2), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(1, gameRules.calculateScore(player1, Point(2, 2)))
        assertEquals(13, player1.scoreTrack.score)

        //--------------------------------------------------

        tilesMap.apply {
            put(Point(2, 1), Tile(Random(999).nextInt(), TileType.RED))
        }

        assertEquals(6, gameRules.calculateScore(player1, Point(1, 2)))
        assertEquals(19, player1.scoreTrack.score)
    }

    @Test
    fun calculateExtraScore() {
        val tilesMap = player1.wall.tilesMap

        tilesMap.apply {
            putTile(Point(1, 0), tilesMap)
            putTile(Point(1, 1), tilesMap)
            putTile(Point(1, 2), tilesMap)
            putTile(Point(1, 3), tilesMap)
            putTile(Point(1, 4), tilesMap)

            putTile(Point(0, 3), tilesMap)
            putTile(Point(2, 3), tilesMap)
            putTile(Point(3, 3), tilesMap)
            putTile(Point(4, 3), tilesMap)

            putTile(Point(0, 4), tilesMap)
            putTile(Point(2, 4), tilesMap)
            putTile(Point(3, 4), tilesMap)
            putTile(Point(4, 4), tilesMap)

            putTile(Point(0, 2), tilesMap)
            putTile(Point(3, 0), tilesMap)
            putTile(Point(4, 1), tilesMap)
        }

        gameRules.calculateExtraScore(player1)

        assertEquals(21, player1.scoreTrack.score)
    }

    private fun putTile(
        point: Point,
        tilesMap: MutableMap<Point, Tile?>
    ) {
        tilesMap[point] = Tile(Random(9999).nextInt(), WallMatcher.getColorForPoint(point.column, point.row))
    }
}