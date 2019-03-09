package com.monstar.azul.presentation.view

import com.monstar.azul.data.entities.Tile

interface OnTileClickListener {
    fun onTileClicked(tile: Tile)
}