package com.monstar.azul.presentation.game

import com.monstar.azul.data.entities.*
import com.monstar.azul.domain.GameRules

interface GameView {
    fun initCircles(circles: List<Circle>)
    fun initTable(table: Table)
    fun updateCircles(circle: Circle)
    fun selectTilesOnTable(tileType: TileType)
    fun selectTilesOnCircle(circle: Circle, tileType: TileType)
    fun updateTable(table: Table)
    fun clearTilesSelection()
    fun setCurrentPlayer(player: Player, notifyRemoteDevices: Boolean)
    fun initFloorLine(floorLine: FloorLine)
}

interface GamePresenter {

    fun initGame(game: Game)
    fun onTileClicked(tile: Tile)
    fun onPatterLineClicked(line: Line)
    fun onFloorLineClicked()

    val game: Game
    fun updateGame(game: Game)
}

class GamePresenterImpl : GamePresenter {
    lateinit var view: GameView

    override lateinit var game: Game
    private val table = { game.table }
    private val circles = { game.circles }

    private var selectedTilesOnTable: Boolean = false
    private var selectedCircle: Circle? = null
    private var selectedTileType: TileType? = null
    private var selectedTiles: List<Tile>? = null

    private val gameRules = GameRules()

    override fun initGame(game: Game) {
        this.game = game

        view.initCircles(circles())
        view.initTable(table())
        view.setCurrentPlayer(game.currentPlayer, false)
    }

    override fun updateGame(game: Game) {
        this.game = game

        circles().forEach {
            view.updateCircles(it)
        }

        view.updateTable(table())
        view.setCurrentPlayer(game.currentPlayer, false)
    }

    override fun onTileClicked(tile: Tile) {
        view.clearTilesSelection()

        val circleTile = tilesOnCircles().find { it == tile }

        if (circleTile != null) {
            val circle = circles().find { circle -> circle.tiles.find { tile == it } != null }

            selectedTiles = circle!!.tiles.filter { it.tileType == circleTile.tileType }
            selectedCircle = circle
            selectedTilesOnTable = false
            selectedTileType = circleTile.tileType

            view.selectTilesOnCircle(circle, circleTile.tileType)
        } else {
            val tableTile = table().tiles.find { it == tile }!!

            selectedTiles = table().tiles.filter { it.tileType == tableTile.tileType }
            selectedTilesOnTable = true
            selectedCircle = null
            selectedTileType = tableTile.tileType

            view.selectTilesOnTable(tableTile.tileType)
        }
    }

    override fun onPatterLineClicked(line: Line) {
        if (selectedTiles.isNullOrEmpty()) {
            return
        }

        val tileType = selectedTiles!![0].tileType

        if (selectedCircle != null) {
            gameRules.getTilesFromCircleAndPlaceToLine(
                table(),
                game.currentPlayer,
                selectedCircle!!,
                tileType,
                line
            )

            view.updateCircles(selectedCircle!!)
        } else if (selectedTilesOnTable) {
            gameRules.getTilesFromTableAndPlaceToLine(
                game,
                game.currentPlayer,
                selectedTileType!!,
                line
            )
        }

        tilesPlaced()
    }

    private fun tilesPlaced() {
        view.clearTilesSelection()
        view.updateTable(table())

        selectedTilesOnTable = false
        selectedTileType = null
        selectedTiles = null
        selectedCircle = null

        if (gameRules.nextRoundShouldStart(game)) {
            gameRules.startNextRound(game)

            view.updateTable(table())
            circles().forEach {
                view.updateCircles(it)
            }

            nextPlayerTurn(true)
        } else {
            nextPlayerTurn(false)
        }
    }

    override fun onFloorLineClicked() {
        if (selectedTiles.isNullOrEmpty()) {
            return
        }

        val tileType = selectedTiles!![0].tileType

        if (selectedCircle != null) {
            gameRules.getTilesFromCircleAndPlaceToFloor(
                table(),
                game.currentPlayer,
                selectedCircle!!,
                tileType
            )

            view.updateCircles(selectedCircle!!)
        } else if (selectedTilesOnTable) {
            gameRules.getTilesFromTableAndPlaceToFloor(
                game,
                game.currentPlayer,
                selectedTileType!!
            )
        }

        tilesPlaced()
    }

    private fun nextPlayerTurn(isFirstTurn: Boolean) {
        if (!isFirstTurn) {
            gameRules.nextPlayerTurn(game)
        }


        view.setCurrentPlayer(game.currentPlayer, true)
    }

    private fun tilesOnCircles(): List<Tile> {
        return circles().flatMap { it.tiles }
    }

}