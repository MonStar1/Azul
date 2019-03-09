package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.monstar.azul.R
import com.monstar.azul.data.entities.Player
import com.monstar.azul.domain.util.findViewInDept
import kotlinx.android.synthetic.main.player_view.view.*

class PlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onLineCellClickListener: OnLineCellClickListener? = null
        set(value) {
            patternLineViews.forEach { it.onLineCellClickListener = value }
            field = value
        }

    lateinit var player: Player

    val patternLineViews by lazy {
        findViewInDept { it is LineCellView } as MutableList<LineCellView>
    }

    init {
        View.inflate(context, R.layout.player_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        patternLineViews.forEach { it.onLineCellClickListener = onLineCellClickListener }
    }

    fun update() {
        patternLineViews.forEach { it.update() }

        playerTurn.text = player.profile.name

        val patternLines = player.patternLines

        patternLineViews.forEachIndexed { index, lineCellView ->
            lineCellView.line = patternLines.lines[index]

            lineCellView.update()
        }

        floorLineView.floorLine = player.floorLine
        floorLineView.update()

        wallView.wall = player.wall
        wallView.update()

        scoreTrack.text = player.scoreTrack.score.toString()
    }
}