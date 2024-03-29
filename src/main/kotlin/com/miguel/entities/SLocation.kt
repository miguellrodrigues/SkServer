package com.miguel.entities

import org.bukkit.Bukkit
import org.bukkit.Location

data class SLocation(
    val id: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double
) {

    fun toBukkitLocation(): Location {
        return Location(
            Bukkit.getWorld(world),
            x,
            y,
            z
        )
    }

    override fun toString(): String {
        return "SLocation(id=$id, world='$world', x=$x, y=$y, z=$z)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SLocation

        if (id != other.id) return false
        if (world != other.world) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + world.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}