package com.miguel.entities

import java.util.*

data class SAccount(
    val id: Int = 0,
    val player_id: UUID = UUID.randomUUID(),
    var balance: Double = .0
) {
    override fun toString(): String {
        return "SAccount(id=$id, player_id=$player_id, balance=$balance)"
    }
}