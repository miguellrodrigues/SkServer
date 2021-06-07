package com.miguel.game.home

import java.util.*

interface IHomeDTO {
    val x: Double
    val y: Double
    val z: Double

    val world: String
}

data class HomeLocation(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val world: String
) : IHomeDTO

class Home(val name: String, val location: HomeLocation, val owner: UUID) {

    override fun equals(other: Any?): Boolean {
        if (other is Home) {
            return other.name == name &&
                    other.owner == owner &&
                    other.location == location
        }

        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }
}