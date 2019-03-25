package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.Toast
import com.monstar.azul.R
import com.monstar.azul.data.entities.Line
import com.monstar.azul.domain.GameRuleException
import com.monstar.azul.domain.util.findViewInDept

class LineCellView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var line: Line? = null
        set(value) {
            removeAllViews()

            repeat(value?.lineQuality ?: 0) {
                addView(TileView(context).apply {
                    layoutParams = LayoutParams(
                        context.resources.getDimensionPixelSize(R.dimen.cell_size),
                        context.resources.getDimensionPixelSize(R.dimen.cell_size)
                    )
                })
            }

            field = value
        }

    var onLineCellClickListener: OnLineCellClickListener? = null

    private fun cells(): MutableList<TileView> {
        return findViewInDept { it is TileView } as MutableList<TileView>
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnClickListener {
            try {
                onLineCellClickListener?.onLineCellClicked(this)
            } catch (ex: Throwable) {
                if (ex is GameRuleException.GameOver) {
                    Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
                    val result = ex.players.map { it.profile.name + " - Очки: " + it.scoreTrack.score }

                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun update() {
        line?.tiles?.forEachIndexed { index, tile ->
            cells()[index].update(tile)
        }
    }
}

interface OnLineCellClickListener {
    fun onLineCellClicked(lineCellView: LineCellView)
}