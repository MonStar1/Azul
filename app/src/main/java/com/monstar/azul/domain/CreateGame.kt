package com.monstar.azul.domain

import com.monstar.azul.data.entities.*
import com.monstar.azul.domain.util.fillUnique
import java.util.*

class CreateGame {

    fun createProfile(name: String): Profile {
        return Profile(UUID.randomUUID().toString().hashCode(), name)
    }

    fun createPlayer(profile: Profile): Player {
        return Player(
            profile,
            createPatternLines(),
            createWall(),
            createFloorLine(),
            createScoreTrack()
        )
    }

    fun createGame(players: List<Player>): Game {
        val firstPlayer = players.random()
        return Game(
            players,
            createTable(),
            createCircles(players.size),
            createBag(),
            firstPlayer,
            firstPlayer
        )
    }

    private fun createTable(): Table {
        return Table(mutableListOf(), true)
    }

    private fun createCircles(numberOfPlayers: Int): List<Circle> {
        val numberOfCircles = when (numberOfPlayers) {
            1 -> 3
            2 -> 5
            3 -> 7
            4 -> 9
            else -> throw Throwable("Game doesn't support $numberOfPlayers players")
        }

        return arrayOfNulls<Circle>(numberOfCircles).run {
            fillUnique { index -> Circle(index, mutableListOf()) }
            toList() as List<Circle>
        }
    }

    private fun createBag(): Bag {
        val bagOfTiles = arrayOfNulls<Tile>(100).run {
            fillUnique(0, 20) { index -> Tile(index, TileType.RED) }
            fillUnique(20, 40) { index -> Tile(index, TileType.BLUE) }
            fillUnique(40, 60) { index -> Tile(index, TileType.BLACK) }
            fillUnique(60, 80) { index -> Tile(index, TileType.YELLOW) }
            fillUnique(80, 100) { index -> Tile(index, TileType.GREEN) }
            toMutableList().shuffled()
        }

        return Bag(bagOfTiles as MutableList<Tile>, mutableListOf())
    }

    private fun createWall(): Wall {
        return Wall(mutableMapOf())
    }

    private fun createScoreTrack(): ScoreTrack {
        return ScoreTrack(0)
    }

    private fun createPatternLines(): PatternLines {
        return PatternLines(
            listOf(
                Line(0, 1, mutableListOf()),
                Line(1, 2, mutableListOf()),
                Line(2, 3, mutableListOf()),
                Line(3, 4, mutableListOf()),
                Line(4, 5, mutableListOf())
            )
        )
    }

    private fun createFloorLine(): FloorLine {
        return FloorLine(mutableListOf())
    }
}