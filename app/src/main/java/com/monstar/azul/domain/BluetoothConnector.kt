package com.monstar.azul.domain

import com.github.ivbaranov.rxbluetooth.BluetoothConnection
import com.google.gson.Gson
import com.monstar.azul.data.entities.Game
import io.reactivex.Completable
import io.reactivex.Observable


val UUID_AZUL = java.util.UUID.fromString("b2e348a4-1467-4812-a60f-b194d3f99a27")

class BluetoothConnector(private val connection: BluetoothConnection, val isServer: Boolean) {

    fun sendGame(game: Game): Completable {
        return Completable.create {
            val gameString = Gson().toJson(game)
            connection.send(gameString + "\n")
        }
    }

    fun getDataFrom(): Observable<Game> {
        return connection.observeStringStream().map {
            Gson().fromJson(it, Game::class.java)
        }.toObservable()
    }
}