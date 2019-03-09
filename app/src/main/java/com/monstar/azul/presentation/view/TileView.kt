package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.monstar.azul.R
import com.monstar.azul.data.entities.Tile
import com.monstar.azul.data.entities.TileType
import kotlinx.android.synthetic.main.tile_view.view.*

class TileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onTileClickListener: OnTileClickListener? = null
        set(value) {
            idTile?.setOnClickListener { if (tile != null) value?.onTileClicked(tile!!) }

            field = value
        }

    init {
        View.inflate(context, R.layout.tile_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        idTile.foreground = context.getDrawable(R.drawable.tile_foreground)

        if (onTileClickListener != null) {
            idTile?.setOnClickListener { if (tile != null) onTileClickListener?.onTileClicked(tile!!) }
        }
    }

    private var tile: Tile? = null

    fun setTile(tile: Tile?) {
        this.tile = tile

        when (tile?.tileType) {
            TileType.RED -> idTile.setBackgroundResource(R.color.red_tile)
            TileType.BLUE -> idTile.setBackgroundResource(R.color.blue_tile)
            TileType.YELLOW -> idTile.setBackgroundResource(R.color.orange_tile)
            TileType.BLACK -> idTile.setBackgroundResource(R.color.black_tile)
            TileType.GREEN -> idTile.setBackgroundResource(R.color.green_tile)
            TileType.FIRST -> idTile.background = context.getDrawable(R.drawable.first)
            else -> idTile.background = null
        }
    }

    fun getTile() = tile

    fun clear() {
        setTile(null)
    }

    override fun setSelected(selected: Boolean) {
        idTile.isSelected = selected
    }
}