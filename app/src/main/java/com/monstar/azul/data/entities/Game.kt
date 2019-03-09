package com.monstar.azul.data.entities

data class Game(
    val players: List<Player>,
    val table: Table,
    val circles: List<Circle>,
    val bag: Bag,
    var firstPlayer: Player,
    var currentPlayer: Player
)