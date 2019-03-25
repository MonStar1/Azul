package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.children
import com.monstar.azul.R
import com.monstar.azul.data.entities.FirstTile
import com.monstar.azul.data.entities.Table
import com.monstar.azul.data.entities.TileType
import kotlinx.android.synthetic.main.table_view.view.*

class TableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.table_view, this)
    }

    var onTileClickListener: OnTileClickListener? = null

    var table: Table? = null

    fun selectTiles(tileType: TileType) {
        idTable.children.forEach {
            it.isSelected = (it as TileView).getTile()?.tileType == tileType
        }
    }

    fun updateTable(table: Table) {
        this.table = table

        idTable.removeAllViews()

        if (table.hasFirstTile) {
            idTable.addView(TileView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = context.resources.getDimensionPixelSize(R.dimen.cell_size)
                    height = context.resources.getDimensionPixelSize(R.dimen.cell_size)

                    setMargins(4, 4, 4, 4)
                }

                update(FirstTile)
            })
        }

        if (table.tiles.isNotEmpty()) {
            table.tiles.forEach { tile ->
                idTable.addView(TileView(context).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = context.resources.getDimensionPixelSize(R.dimen.cell_size)
                        height = context.resources.getDimensionPixelSize(R.dimen.cell_size)

                        setMargins(4, 4, 4, 4)
                    }
                    update(tile)
                    this@apply.onTileClickListener = this@TableView.onTileClickListener
                })
            }
        }
    }

    fun clearTilesSelection() {
        idTable.children.forEach { it.isSelected = false }
    }
}