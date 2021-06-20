package com.miguel.entities

import java.util.*

class SHome(
    val owner: UUID,
    val location: SLocation
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SHome

        if (location != other.location) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }
}