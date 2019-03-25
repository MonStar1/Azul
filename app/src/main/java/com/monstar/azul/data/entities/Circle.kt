package com.monstar.azul.data.entities

class Circle(val id: Int, val tiles: MutableList<Tile>) {

    override fun equals(other: Any?): Boolean {
        if (other !is Circle) {
            return false
        }

        return other.id == this.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + id
        return result
    }
}