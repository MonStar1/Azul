package com.monstar.azul.data.entities

data class FloorLine(val tiles: MutableList<Tile>) {
    operator fun get(i: Int) = tiles[i]
}