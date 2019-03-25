package com.monstar.azul.domain

import com.monstar.azul.data.entities.*
import com.monstar.azul.domain.util.findAndRemove
import com.monstar.azul.domain.util.getAllAndRemove
import com.monstar.azul.domain.util.getFirstItemsAndRemove
import kotlin.math.min

sealed class GameRuleException(message: String) : Throwable(message) {
    class LineIsFull(line: Line) : GameRuleException("Line $line is full")
    class LineContainsOtherColor(line: Line, tileType: TileType) :
        GameRuleException("Line $line contain ${tileType.name} type of tiles")

    class CantFindLine(line: Line) : GameRuleException("Can't find such line: $line ")
    class CircleDoesNotContainTiles(circle: Circle, tiles: List<Tile>) :
        GameRuleException("Circle with tiles: ${circle.tiles} doesn't contain all tiles: $tiles")

    class TileExistsOnWallLine(tileType: TileType, linePosition: Int) :
        GameRuleException("Tile $tileType already exists on wall's line: ${linePosition + 1}")

    class NextRoundCantBeStarted : GameRuleException("Next round can't be started")
    class GameOver(winner: Player, val players: List<Player>) :
        GameRuleException("Game Over. Winner !! ${winner.profile.name.toUpperCase()} !! Score: ${winner.scoreTrack.score}")
}

class GameRules {

    fun fillAllCircles(game: Game) {
        val circles = game.circles
        val bag = game.bag

        circles.forEach {
            it.tiles.clear()
            it.tiles.addAll(bag.getFromBagAndRemove(4))
        }
    }

    fun getTilesFromTableAndPlaceToLine(game: Game, player: Player, tileType: TileType, line: Line) {
        validateCanPlayerFillLine(line, tileType, player.wall)

        val getTiles = getItemsFromTable(game, player, tileType)

        placeTilesToLine(line, getTiles, player)
    }

    fun getTilesFromTableAndPlaceToFloor(game: Game, player: Player, tileType: TileType) {
        val getTiles = getItemsFromTable(game, player, tileType)

        placeTilesToFloor(getTiles, player)
    }

    private fun getItemsFromTable(
        game: Game,
        player: Player,
        tileType: TileType
    ): MutableList<Tile> {
        val table = game.table

        if (table.hasFirstTile) {
            player.floorLine.tiles.add(FirstTile)
            table.hasFirstTile = false

            game.firstPlayer = player
        }

        return table.tiles.findAndRemove { it.tileType == tileType }
    }

    fun getTilesFromCircleAndPlaceToLine(
        table: Table,
        player: Player,
        circle: Circle,
        getTileType: TileType,
        line: Line
    ) {
        validateCanPlayerFillLine(line, getTileType, player.wall)

        val getTiles = getTilesFromCircle(getTileType, circle, table)

        placeTilesToLine(line, getTiles, player)
    }

    fun getTilesFromCircleAndPlaceToFloor(
        table: Table,
        player: Player,
        circle: Circle,
        getTileType: TileType
    ) {
        val getTiles = getTilesFromCircle(getTileType, circle, table)

        placeTilesToFloor(getTiles, player)
    }

    private fun placeTilesToLine(
        line: Line,
        getTiles: MutableList<Tile>,
        player: Player
    ) {
        //add selected items to selected line
        player.patternLines.lines.find { it == line }?.apply {
            val addCount = min(getTiles.size, lineQuality - tiles.size)
            tiles.addAll(getTiles.getFirstItemsAndRemove(addCount))
        } ?: throw GameRuleException.CantFindLine(line)

        //add remaining items to floor line
        if (getTiles.isNotEmpty()) {
            player.floorLine.tiles.addAll(getTiles.getAllAndRemove())
        }
    }

    private fun placeTilesToFloor(
        getTiles: MutableList<Tile>,
        player: Player
    ) {
        player.floorLine.tiles.addAll(getTiles.getAllAndRemove())
    }

    private fun getTilesFromCircle(
        getTileType: TileType,
        circle: Circle,
        table: Table
    ): MutableList<Tile> {
        val circleTiles = circle.tiles

        //get and remove selected items from the circle
        val getTiles = circleTiles.findAndRemove { it.tileType == getTileType }

        //add remaining items to the table pool
        table.tiles.addAll(circleTiles.getAllAndRemove())

        sortTableItems(table)

        return getTiles
    }

    private fun sortTableItems(table: Table) {
        table.tiles.sortBy { it.tileType }
    }

    private fun validateCanPlayerFillLine(line: Line, tileType: TileType, wall: Wall) {
        val lineTiles = line.tiles

        if (lineTiles.size == line.lineQuality) {
            throw GameRuleException.LineIsFull(line)
        }

        if (lineTiles.isNotEmpty() && lineTiles.first().tileType != tileType) {
            throw GameRuleException.LineContainsOtherColor(line, lineTiles.first().tileType)
        }

        val tileExistsOnWall = wall.tilesMap.filterKeys { it.row == line.linePosition }
            .filterValues { it?.tileType == tileType }.isNotEmpty()

        if (tileExistsOnWall) {
            throw GameRuleException.TileExistsOnWallLine(tileType, line.linePosition)
        }
    }

    fun nextPlayerTurn(game: Game): Player {
        val players = game.players
        val currentIndex = players.indexOfFirst { it == game.currentPlayer }

        if (players.size == currentIndex + 1) {
            game.currentPlayer = players.first()
        } else {
            game.currentPlayer = players[currentIndex + 1]
        }

        return game.currentPlayer
    }

    fun nextRoundShouldStart(game: Game): Boolean {
        val circleTiles = game.circles.flatMap { it.tiles }
        val tableTiles = game.table.tiles

        return circleTiles.isEmpty() && tableTiles.isEmpty()
    }

    fun startNextRound(game: Game) {
        if (!nextRoundShouldStart(game)) {
            throw GameRuleException.NextRoundCantBeStarted()
        }

        game.players.forEach {
            finishPlayerTurn(it, game)
        }

        if (isGameOver(game.players)) {
            gameOver(game)

            val winner = game.players.maxBy { it.scoreTrack.score }!!
            throw GameRuleException.GameOver(winner, game.players)
        } else {
            prepareNextRound(game)
        }
    }

    private fun prepareNextRound(game: Game) {
        game.currentPlayer = game.firstPlayer

        val bag = game.bag

        game.table.hasFirstTile = true

        if (bag.tiles.size < game.circles.size * 4) {
            bag.tiles.addAll(bag.drop.getAllAndRemove())
        }

        fillAllCircles(game)
    }

    private fun finishPlayerTurn(
        player: Player,
        game: Game
    ) {
        finishPatternLines(player, game)
        finishFloorLine(player, player.floorLine, game)
    }

    private fun finishFloorLine(
        player: Player,
        floorLine: FloorLine,
        game: Game
    ) {
        calculateScore(player, floorLine)

        val floorTiles = floorLine.tiles

        floorTiles.remove(FirstTile)

        game.bag.drop.addAll(floorTiles.getAllAndRemove())
    }

    private fun finishPatternLines(
        player: Player,
        game: Game
    ) {
        val patternLines = player.patternLines
        val wall = player.wall

        patternLines.lines.forEachIndexed { index, line ->
            if (line.lineQuality == line.tiles.size) {
                val point = WallMatcher.getPointForColor(index, line.tiles.first().tileType)

                wall.tilesMap[point] = line.tiles.removeAt(0)

                game.bag.drop.addAll(line.tiles.getAllAndRemove())

                calculateScore(player, point)
            }
        }
    }

    private fun calculateScore(player: Player, floorLine: FloorLine) {
        player.scoreTrack.score -= when (floorLine.tiles.size) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 4
            4 -> 6
            5 -> 8
            6 -> 11
            7 -> 14
            8 -> 17
            else -> 20
        }

        checkScoreLessThanZero(player)
    }

    fun calculateScore(player: Player, placedPoint: Point): Int {
        val column = placedPoint.column
        val row = placedPoint.row

        val tilesMap = player.wall.tilesMap

        var score: Int
        var scoreVertical = 0
        var scoreHorizontal = 0

        val horizontalMap = tilesMap.filterKeys { it.row == row }
        val verticalMap = tilesMap.filterKeys { it.column == column }

        var counter = 1
        var ignoreLeft = false
        var ignoreRight = false

        while (counter < 5) {
            val leftX = column - counter
            val rightX = column + counter

            if (!ignoreLeft && leftX >= 0 && horizontalMap[Point(leftX, row)] != null) {
                scoreHorizontal++
            } else {
                ignoreLeft = true
            }

            if (!ignoreRight && rightX <= 4 && horizontalMap[Point(rightX, row)] != null) {
                scoreHorizontal++
            } else {
                ignoreRight = true
            }

            counter++
        }

        counter = 1
        ignoreLeft = false
        ignoreRight = false

        while (counter < 5) {
            val topY = row - counter
            val bottomY = row + counter

            if (!ignoreLeft && topY >= 0 && verticalMap[Point(column, topY)] != null) {
                scoreVertical++
            } else {
                ignoreLeft = true
            }

            if (!ignoreRight && bottomY <= 4 && verticalMap[Point(column, bottomY)] != null) {
                scoreVertical++
            } else {
                ignoreRight = true
            }

            counter++
        }

        if (scoreHorizontal > 0) scoreHorizontal++
        if (scoreVertical > 0) scoreVertical++

        score = scoreHorizontal + scoreVertical

        if (score == 0) score = 1

        player.scoreTrack.score += score

        return score
    }

    fun calculateExtraScore(player: Player) {
        val wallMap = player.wall.tilesMap

        TileType.values().forEach { tileType ->
            wallMap.filterValues { it?.tileType == tileType }.let {
                if (it.size == 5) {
                    player.scoreTrack.score += 10
                }
            }
        }

        for (i in 0..5) {
            wallMap.filterKeys { it.row == i }.filterValues { it != null }.let {
                if (it.size == 5) {
                    player.scoreTrack.score += 2
                }
            }

            wallMap.filterKeys { it.column == i }.filterValues { it != null }.let {
                if (it.size == 5) {
                    player.scoreTrack.score += 7
                }
            }
        }
    }

    private fun isGameOver(player: List<Player>): Boolean {
        player.filter { isGameOver(it) }.let {
            if (it.isNotEmpty()) {
                return true
            }
        }

        return false
    }

    private fun isGameOver(player: Player): Boolean {
        val wallMap = player.wall.tilesMap

        for (i in 0..5) {
            wallMap.filterKeys { it.row == i }.filterValues { it != null }.let {
                if (it.size == 5) {
                    return true
                }
            }
        }

        return false
    }

    private fun gameOver(game: Game) {
        game.players.forEach {
            calculateExtraScore(it)
        }
    }

    private fun checkScoreLessThanZero(player: Player) {
        if (player.scoreTrack.score < 0) player.scoreTrack.score = 0
    }

}