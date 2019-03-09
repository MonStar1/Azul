package com.monstar.azul.domain

import com.monstar.azul.data.entities.Point
import com.monstar.azul.data.entities.TileType

object WallMatcher {
    fun getPointForColor(line: Int, tileType: TileType): Point {
        return when (line) {
            0 -> when (tileType) {
                TileType.BLUE -> Point(0, line)
                TileType.YELLOW -> Point(1, line)
                TileType.RED -> Point(2, line)
                TileType.BLACK -> Point(3, line)
                TileType.GREEN -> Point(4, line)
                else -> TODO("ignore")
            }
            1 -> when (tileType) {
                TileType.BLUE -> Point(1, line)
                TileType.YELLOW -> Point(2, line)
                TileType.RED -> Point(3, line)
                TileType.BLACK -> Point(4, line)
                TileType.GREEN -> Point(0, line)
                else -> TODO("ignore")
            }
            2 -> when (tileType) {
                TileType.BLUE -> Point(2, line)
                TileType.YELLOW -> Point(3, line)
                TileType.RED -> Point(4, line)
                TileType.BLACK -> Point(0, line)
                TileType.GREEN -> Point(1, line)
                else -> TODO("ignore")
            }
            3 -> when (tileType) {
                TileType.BLUE -> Point(3, line)
                TileType.YELLOW -> Point(4, line)
                TileType.RED -> Point(0, line)
                TileType.BLACK -> Point(1, line)
                TileType.GREEN -> Point(2, line)
                else -> TODO("ignore")
            }
            4 -> when (tileType) {
                TileType.BLUE -> Point(4, line)
                TileType.YELLOW -> Point(0, line)
                TileType.RED -> Point(1, line)
                TileType.BLACK -> Point(2, line)
                TileType.GREEN -> Point(3, line)
                else -> TODO("ignore")
            }
            else -> TODO("ignore")
        }
    }

    fun getColorForPoint(column: Int, row: Int): TileType {
        return when (row) {
            0 -> when (column) {
                0 -> TileType.BLUE
                1 -> TileType.YELLOW
                2 -> TileType.RED
                3 -> TileType.BLACK
                4 -> TileType.GREEN
                else -> TODO("ignore")
            }
            1 -> when (column) {
                0 -> TileType.GREEN
                1 -> TileType.BLUE
                2 -> TileType.YELLOW
                3 -> TileType.RED
                4 -> TileType.BLACK
                else -> TODO("ignore")
            }
            2 -> when (column) {
                0 -> TileType.BLACK
                1 -> TileType.GREEN
                2 -> TileType.BLUE
                3 -> TileType.YELLOW
                4 -> TileType.RED
                else -> TODO("ignore")
            }
            3 -> when (column) {
                0 -> TileType.RED
                1 -> TileType.BLACK
                2 -> TileType.GREEN
                3 -> TileType.BLUE
                4 -> TileType.YELLOW
                else -> TODO("ignore")
            }
            4 -> when (column) {
                0 -> TileType.YELLOW
                1 -> TileType.RED
                2 -> TileType.BLACK
                3 -> TileType.GREEN
                4 -> TileType.BLUE
                else -> TODO("ignore")
            }
            else -> TODO("ignore")
        }
    }
}