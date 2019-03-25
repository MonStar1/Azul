package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.monstar.azul.R
import com.monstar.azul.data.entities.FloorLine
import com.monstar.azul.domain.util.findViewInDept

class FloorLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var floorLine: FloorLine? = null

    init {
        View.inflate(context, R.layout.floor_line_view, this)
    }

    private val cells by lazy { findViewInDept { it is TileView } as MutableList<TileView> }

    fun update(floorLine: FloorLine) {
        this.floorLine = floorLine

        cells.forEach {
            it.clear()
        }

        floorLine.tiles.forEachIndexed { index, tile ->
            cells[index].update(tile)
        }
    }
}