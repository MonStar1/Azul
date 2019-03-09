package com.monstar.azul.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.monstar.azul.R
import com.monstar.azul.data.entities.*
import com.monstar.azul.domain.GameRuleException
import com.monstar.azul.domain.util.findViewInDept
import com.monstar.azul.presentation.game.GamePresenter
import com.monstar.azul.presentation.game.GameView
import kotlinx.android.synthetic.main.game_view.view.*
import kotlinx.android.synthetic.main.player_view.view.*

class GameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnTileClickListener, OnLineCellClickListener, GameView {

    init {
        View.inflate(context, R.layout.game_view, this)
    }

    val circleViews by lazy {
        findViewInDept { it is CircleView } as MutableList<CircleView>
    }

    lateinit var presenter: GamePresenter

    override fun initCircles(circles: List<Circle>) {
        circles.forEach {
            val circleView = getCircleView(it)

            circleView.layoutParams = LinearLayout.LayoutParams(0, 300, 1F)

            circlesLayout.addView(circleView)
        }

        circleViews.forEach { it.onTileClickListener = this }
    }

    override fun initTable(table: Table) {
        tableView.table = table
        tableView.updateTable()

        tableView.onTileClickListener = this
    }

    override fun initFloorLine(floorLine: FloorLine) {
        floorLineView.floorLine = floorLine
    }

    override fun updateCircles(circle: Circle) {
        circleViews.first { it.circle == circle }.update()
    }

    override fun selectTilesOnTable(tileType: TileType) {
        tableView.selectTiles(tileType)
    }

    override fun selectTilesOnCircle(circle: Circle, tileType: TileType) {
        circleViews.first { it.circle == circle }.selectTiles(tileType)
    }

    override fun updateTable() {
        tableView.updateTable()
    }

    override fun clearTilesSelection() {
        circleViews.forEach { it.clearTilesSelection() }
        tableView.clearTilesSelection()
    }

    override fun setCurrentPlayer(player: Player) {
        playerView.player = player
        playerView.update()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        playerView.onLineCellClickListener = this

        floorLineView.setOnClickListener {
            try {
                presenter.onFloorLineClicked()
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

        showOverlay.setOnTouchListener { v, event ->
            val player = presenter.game.players.filter { it != presenter.game.currentPlayer }.first()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    overlayPlayer.player = player
                    overlayPlayer.update()
                    overlayPlayer.visibility = View.VISIBLE

                    true
                }

                MotionEvent.ACTION_UP -> {
                    overlayPlayer.visibility = View.GONE

                    true
                }

                else -> false
            }
        }
    }

    private fun getCircleView(circle: Circle): CircleView {
        return CircleView(context).apply {
            this.circle = circle
        }
    }

    override fun onTileClicked(tile: Tile) {
        presenter.onTileClicked(tile)
    }

    override fun onLineCellClicked(lineCellView: LineCellView) {
        presenter.onPatterLineClicked(lineCellView.line!!)
    }
}