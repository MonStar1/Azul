package com.monstar.azul.data.entities

class Player(
    val profile: Profile,
    val patternLines: PatternLines,
    val wall: Wall,
    val floorLine: FloorLine,
    val scoreTrack: ScoreTrack
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Player) {
            return false
        }

        return other.profile == this.profile
    }

    override fun hashCode(): Int {
        return profile.hashCode()
    }
}