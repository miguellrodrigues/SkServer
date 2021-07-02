package com.miguel.entities

import java.util.*

class SHome(
    val id: Int = 0,
    val name: String,
    var player_id: UUID = UUID.randomUUID(),
    val location: SLocation = SLocation(0, "", "world", .0, .0, .0),
    var delete: Boolean = false
) {

    override fun toString(): String {
        return "SHome(id=$id, name='$name', player_id=$player_id, location=$location, delete=$delete)"
    }
}