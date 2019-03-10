package com.monstar.azul.presentation.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.monstar.azul.R
import com.monstar.azul.data.entities.Wall
import com.monstar.azul.domain.util.findViewInDept

class WallView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var wall: Wall? = null

    init {
        inflateView(context)
    }

    private fun inflateView(context: Context) {
        View.inflate(context, R.layout.wall_view, this)
    }

    private fun cells() = findViewInDept { it is TileView } as MutableList<TileView>

    fun update() {
        removeAllViews()
        inflateView(context)

        wall?.tilesMap?.forEach {
            val index = it.key.row * 5 + it.key.column

            cells()[index].setTile(it.value)
        }
    }
}