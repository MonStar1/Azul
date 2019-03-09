package com.monstar.azul.data.entities

enum class TileType {
    RED,
    BLUE,
    YELLOW,
    BLACK,
    GREEN,
    FIRST
}

data class Tile(val id: Int, val tileType: TileType)

val FirstTile = Tile(101, TileType.FIRST)