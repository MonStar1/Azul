package com.monstar.azul.presentation.view

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.monstar.azul.R
import com.monstar.azul.data.entities.Circle
import com.monstar.azul.data.entities.Tile
import com.monstar.azul.data.entities.TileType
import kotlinx.android.synthetic.main.circle_view.view.*

class CircleView constructor(
    context: Context
) : FrameLayout(context, null, 0) {

    var circle: Circle? = null
        set(value) {
            fillCircle(value!!)
            field = value
        }

    var onTileClickListener: OnTileClickListener? = null
        set(value) {
            tileViews.forEach {
                it.onTileClickListener = value
            }
            field = value
        }

    private val tileViews by lazy {
        mutableListOf(tile1, tile2, tile3, tile4) as MutableList<TileView>
    }

    init {
        View.inflate(context, R.layout.circle_view, this)
    }

    private fun fillCircle(circle: Circle) {
        fillCircle(circle.tiles)
    }

    private fun fillCircle(tiles: List<Tile>) {
        (tile1 as TileView).setTile(tiles[0])
        (tile2 as TileView).setTile(tiles[1])
        (tile3 as TileView).setTile(tiles[2])
        (tile4 as TileView).setTile(tiles[3])

        tileViews.forEach {
            it.onTileClickListener = onTileClickListener
        }
    }

    fun clearTilesSelection() {
        tileViews.forEach { it.isSelected = false }
    }

    private fun removeAllTiles() {
        tileViews.forEach {
            it.setTile(null)
            it.onTileClickListener = null
        }
    }

    fun selectTiles(tileType: TileType) {
        tileViews.filter { it.getTile()?.tileType == tileType }.forEach {
            it.isSelected = true
        }
    }

    fun update() {
        if (circle?.tiles?.isEmpty() == true) {
            removeAllTiles()
        } else {
            fillCircle(circle!!)
        }
    }
}