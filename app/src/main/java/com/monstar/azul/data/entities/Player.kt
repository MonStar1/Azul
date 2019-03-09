package com.monstar.azul.data.entities

data class Player(
    val profile: Profile,
    val patternLines: PatternLines,
    val wall: Wall,
    val floorLine: FloorLine,
    val scoreTrack: ScoreTrack
)